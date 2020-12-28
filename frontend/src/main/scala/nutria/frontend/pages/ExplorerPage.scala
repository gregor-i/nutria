package nutria.frontend.pages

import monocle.Lens
import monocle.macros.Lenses
import nutria.api._
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import nutria.frontend.service.FractalService
import nutria.frontend.util.{LenseUtils, SnabbdomUtil}
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

@Lenses
case class ExplorerState(
    remoteFractal: Option[FractalImageEntityWithId],
    fractalImage: FractalImageEntity,
    saveModal: Option[SaveFractalDialog] = None,
    editModal: Option[Unit] = None
) extends PageState {
  def dirty: Boolean = remoteFractal.fold(true)(_.entity != fractalImage)
}

@Lenses
case class SaveFractalDialog(
    dimensions: Dimensions,
    antiAliase: AntiAliase
)

object SaveFractalDialog {
  val initial = SaveFractalDialog(
    dimensions = Dimensions.fullHD,
    antiAliase = 2
  )
}

object ExplorerState {
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalImage.composeLens(Entity.value).composeLens(FractalImage.viewport)
}

object ExplorerPage extends Page[ExplorerState] {

  override def stateFromUrl = {
    case (_, s"/fractals/${fractalId}/explorer", queryParams) =>
      (for {
        remoteFractal <- FractalService.get(fractalId)
      } yield {
        val fractalFromUrl =
          queryParams
            .get("state")
            .flatMap(Router.queryDecoded[FractalImageEntity])
            .getOrElse(remoteFractal.entity)

        val editModalOpen = queryParams.get("edit").map(_.toLowerCase).contains("true")

        ExplorerState(
          remoteFractal = Some(remoteFractal),
          fractalImage = fractalFromUrl,
          editModal = Some(()).filter(_ => editModalOpen)
        )
      }).loading()

    case (_, "/explorer", queryParams) =>
      val fractalFromUrl =
        queryParams.get("state").flatMap(Router.queryDecoded[FractalImageEntity])

      val editModalOpen = queryParams.get("edit").map(_.toLowerCase).contains("true")

      fractalFromUrl match {
        case Some(fractal) => ExplorerState(None, fractal, editModal = Some(()).filter(_ => editModalOpen))
        case None          => ErrorState("Query Parameter is invalid")
      }
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] = {
    val stateQueryParam = Map("state" -> Router.queryEncoded(state.fractalImage))
    val modalQueryParam = if (state.editModal.isDefined) Map("edit" -> "true") else Map.empty[String, String]
    state.remoteFractal match {
      case Some(remoteFractal) if state.dirty => Some(s"/fractals/${remoteFractal.id}/explorer" -> (stateQueryParam ++ modalQueryParam))
      case Some(remoteFractal)                => Some(s"/fractals/${remoteFractal.id}/explorer" -> modalQueryParam)
      case None                               => Some("/explorer"                               -> (stateQueryParam ++ modalQueryParam))
    }
  }

  def render(implicit context: Context) =
    Body()
      .child(Header())
      .child(InteractiveFractal.forImage(ExplorerState.fractalImage.composeLens(Entity.value)))
      .child(renderActionBar())
      .childOptional(saveDialog())
      .childOptional(editDialog())

  def renderActionBar()(implicit context: Context): Node =
    ButtonList
      .right()
      .classes("overlay-bottom-right", "padding")
      .child(buttonSave(context.local.fractalImage))
      .child(buttonOpenEditModal())
      .child(buttonOpenSaveToDiskModal())
      .child(buttonResetViewport())

  def buttonOpenEditModal()(implicit context: Context): Node =
    Button
      .icon(Icons.edit, SnabbdomUtil.modify(ExplorerState.editModal.set(Some(()))))
      .classes("button", "is-rounded")

  def buttonOpenSaveToDiskModal()(implicit context: Context): Node =
    Button
      .icon(Icons.download, SnabbdomUtil.modify(ExplorerState.saveModal.set(Some(SaveFractalDialog.initial))))
      .classes("button", "is-rounded")

  def buttonResetViewport()(implicit context: Context): Node =
    context.local.remoteFractal match {
      case Some(remoteFractal) => Button.icon(Icons.undo, SnabbdomUtil.modify(ExplorerState.viewport.set(remoteFractal.entity.value.viewport)))
      case None                => Button.icon(Icons.undo, SnabbdomUtil.noop).boolAttr("disabled", true)
    }

  def buttonSave(fractalImage: FractalImageEntity)(implicit context: Context): Node =
    Button
      .icon(Icons.snapshot, Actions.saveSnapshot(fractalImage))
      .classes("is-primary", "is-rounded")

  def editDialog()(implicit context: Context): Option[Node] =
    context.local.editModal.map { _ =>
      Modal(closeAction = SnabbdomUtil.modify(ExplorerState.editModal.set(None)))(
        "div.container"
          .child(
            "section.section"
              .child("h1.title.is-1".text(Option(context.local.fractalImage.title).filter(_.nonEmpty).getOrElse("<No Title given>")))
              .child("h2.subtitle".text(context.local.fractalImage.description))
          )
          .child(EntityAttributes.section(ExplorerState.fractalImage))
          .child(
            "section.section".children(
              "h4.title.is-4".text("Parameters:"),
              ParameterForm.list(ExplorerState.fractalImage.composeLens(Entity.value).composeLens(FractalImage.appliedParameters)),
              AAInput(ExplorerState.fractalImage.composeLens(Entity.value).composeLens(FractalImage.antiAliase))
            )
          )
          .child(
            "section.section"
              .child(editModalActions())
          )
      )
    }

  private def editModalActions()(implicit context: Context): Node = {
    def buttonUpdate =
      Button("Update", Icons.save, Actions.updateFractal(context.local.remoteFractal.get.copy(entity = context.local.fractalImage)))
        .classes("is-primary")

    def buttonDelete =
      Button("Delete", Icons.delete, Actions.deleteFractal(context.local.remoteFractal.get.id))
        .classes("is-danger", "is-light")

    def buttonFork =
      Button("Clone", Icons.copy, Actions.saveAsNewFractal(context.local.fractalImage.copy(published = false)))
        .classes("is-primary")

    val buttons: Seq[Node] = context.local.remoteFractal match {
      case Some(remoteFractal) if User.isOwner(context.global.user, remoteFractal) =>
        Seq(buttonDelete, buttonFork, buttonUpdate)

      case _ =>
        Seq(buttonFork)
    }

    ButtonList.right(buttons: _*)
  }

  def saveDialog()(implicit context: Context): Option[Node] =
    context.local.saveModal.map { params =>
      val lensParams     = ExplorerState.saveModal.composeLens(LenseUtils.unsafe(monocle.std.all.some[SaveFractalDialog]))
      val downloadAction = Actions.saveToDisk(context.local.fractalImage.value.copy(antiAliase = params.antiAliase), params.dimensions)

      Modal(closeAction = SnabbdomUtil.modify(ExplorerState.saveModal.set(None)))(
        "div"
          .style("marginBottom", "1.5rem")
          .child("h1.title".text("Render high resolution Image"))
          .child(
            Form.forLens(
              "width",
              description = "Resolution (width) of the rendered image",
              lens = lensParams composeLens SaveFractalDialog.dimensions composeLens Dimensions.width
            )
          )
          .child(
            Form.forLens(
              "height",
              description = "Resolution (height) of the rendered image",
              lens = lensParams composeLens SaveFractalDialog.dimensions composeLens Dimensions.height
            )
          )
          .child(Form.forLens("anti alias", description = "Anti Aliase Factor", lens = lensParams composeLens SaveFractalDialog.antiAliase)),
        ButtonList.right(Button("Download", Icons.download, downloadAction).classes("is-primary"))
      )
    }
}
