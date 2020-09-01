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

@Lenses
case class ExplorerState(
    user: Option[User],
    remoteFractal: Option[FractalImageEntityWithId],
    fractalImage: FractalImageEntity,
    saveModal: Option[SaveFractalDialog] = None,
    navbarExpanded: Boolean = false
) extends NutriaState {
  def dirty: Boolean = remoteFractal.fold(true)(_.entity != fractalImage)
}

@Lenses
case class SaveFractalDialog(
    dimensions: Dimensions,
    antiAliase: AntiAliase
)

object ExplorerState {
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalImage.composeLens(Entity.value).composeLens(FractalImage.viewport)
}

object ExplorerPage extends Page[ExplorerState] {

  override def stateFromUrl = {
    case (user, s"/fractals/${fractalId}/explorer", queryParams) =>
      (for {
        remoteFractal <- FractalService.get(fractalId)
      } yield {
        val fractalFromUrl =
          queryParams
            .get("state")
            .flatMap(Router.queryDecoded[FractalImageEntity])
            .getOrElse(remoteFractal.entity)

        ExplorerState(
          user,
          remoteFractal = Some(remoteFractal),
          fractalImage = fractalFromUrl
        )
      }).loading(user)

    case (user, "/explorer", queryParams) =>
      val fractalFromUrl =
        queryParams.get("state").flatMap(Router.queryDecoded[FractalImageEntity])

      fractalFromUrl match {
        case Some(fractal) => ExplorerState(user, None, fractal)
        case None          => ErrorState(user, "Query Parameter is invalid")
      }
  }

  override def stateToUrl(state: ExplorerPage.State): Option[(Path, QueryParameter)] = {
    val stateQueryParam = Map("state" -> Router.queryEncoded(state.fractalImage))
    state.remoteFractal match {
      case Some(remoteFractal) if state.dirty => Some(s"/fractals/${remoteFractal.id}/explorer" -> stateQueryParam)
      case Some(remoteFractal)                => Some(s"/fractals/${remoteFractal.id}/explorer" -> Map.empty)
      case None                               => Some("/explorer"                               -> stateQueryParam)
    }
  }

  def render(implicit state: ExplorerState, update: NutriaState => Unit) =
    Body()
      .child(Header(ExplorerState.navbarExpanded))
      .child(InteractiveFractal.forImage(ExplorerState.fractalImage.composeLens(Entity.value)))
      .child(renderActionBar())
      .childOptional(saveDialog())

  def renderActionBar()(implicit state: ExplorerState, update: NutriaState => Unit): Node =
    ButtonList()
      .classes("overlay-bottom-right", "padding")
      .child(buttonSave(state.fractalImage))
      .childOptional(state.remoteFractal.map(remoteFractal => buttonGoToDetails(remoteFractal, state.fractalImage)))
      .child(Button.icon(Icons.download, Actions.openSaveToDiskModal))
      .child(buttonResetViewport())

  def buttonGoToDetails(
      remoteFractal: WithId[FractalImageEntity],
      currentFractal: FractalImageEntity
  )(implicit state: ExplorerState, update: NutriaState => Unit) =
    Link(Links.detailsState(remoteFractal, currentFractal, state.user))
      .classes("button", "is-rounded")
      .child(Icons.icon(Icons.edit))

  def buttonResetViewport()(implicit state: State, update: NutriaState => Unit) =
    state.remoteFractal match {
      case Some(remoteFractal) => Button.icon(Icons.undo, SnabbdomUtil.update(ExplorerState.viewport.set(remoteFractal.entity.value.viewport)))
      case None                => Button.icon(Icons.undo, SnabbdomUtil.noop).boolAttr("disabled", true)
    }

  def buttonSave(fractalImage: FractalImageEntity)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button
      .icon(Icons.snapshot, Actions.saveSnapshot(fractalImage))
      .classes("is-primary", "is-rounded")

  def saveDialog()(implicit state: ExplorerState, update: NutriaState => Unit): Option[Node] =
    state.saveModal.map { params =>
      val lensParams     = ExplorerState.saveModal.composeLens(LenseUtils.unsafe(monocle.std.all.some[SaveFractalDialog]))
      val downloadAction = Actions.saveToDisk(state.fractalImage.value.copy(antiAliase = params.antiAliase), params.dimensions)

      Modal(closeAction = Actions.closeSaveToDiskModal)(
        Node("div")
          .style("marginBottom", "1.5rem")
          .child(Node("h1.title").text("Render high resolution Image"))
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
        ButtonList(Button("Download", Icons.download, downloadAction).classes("is-primary"))
      )
    }
}
