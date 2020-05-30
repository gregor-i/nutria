package nutria.frontend.pages

import monocle.Lens
import monocle.macros.Lenses
import nutria.api.{Entity, FractalImageEntity, FractalImageEntityWithId, User, WithId}
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import nutria.frontend.pages.explorer.ExplorerEvents
import nutria.frontend.service.NutriaService
import nutria.frontend.util.LenseUtils
import snabbdom.Node

@Lenses
case class ExplorerState(
    user: Option[User],
    remoteFractal: Option[FractalImageEntityWithId],
    fractalImage: FractalImage,
    saveModal: Option[SaveFractalDialog] = None,
    navbarExpanded: Boolean = false
) extends NutriaState {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

@Lenses
case class SaveFractalDialog(
    dimensions: Dimensions,
    antiAliase: AntiAliase
)

object ExplorerState {
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalImage.composeLens(FractalImage.viewport)
}

object ExplorerPage extends Page[ExplorerState] {

  override def stateFromUrl = {
    case (user, s"/fractals/${fractalId}/explorer", queryParams) =>
      (for {
        remoteFractal <- NutriaService.loadFractal(fractalId)
      } yield {
        val fractalFromUrl =
          queryParams.get("state").flatMap(Router.queryDecoded[FractalImage])

        fractalFromUrl match {
          case Some(image) =>
            ExplorerState(
              user,
              remoteFractal = Some(remoteFractal),
              fractalImage = image
            )
          case None => ErrorState(user, "Query Parameter is invalid")
        }
      }).loading(user)

    case (user, "/explorer", queryParams) =>
      val fractalFromUrl =
        queryParams.get("state").flatMap(Router.queryDecoded[FractalImage])

      fractalFromUrl match {
        case Some(fractal) => ExplorerState(user, None, fractal)
        case None          => ErrorState(user, "Query Parameter is invalid")
      }
  }

  override def stateToUrl(state: ExplorerPage.State): Option[(Path, QueryParameter)] = {
    val stateQueryParam = Map("state" -> Router.queryEncoded(state.fractalImage))
    state.remoteFractal match {
      case Some(remoteFractal) => Some(s"/fractals/${remoteFractal.id}/explorer" -> stateQueryParam)
      case None                => Some("/explorer"                               -> stateQueryParam)
    }
  }

  def render(implicit state: ExplorerState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(InteractiveFractal.forImage(ExplorerState.fractalImage))
      .child(renderActionBar())
      .childOptional(saveDialog())

  def renderActionBar()(implicit state: ExplorerState, update: NutriaState => Unit): Node =
    ButtonList()
      .classes("overlay-bottom-right", "padding")
      .child(
        state.remoteFractal match {
          case Some(remoteFractal) => Some(buttonGoToDetails(remoteFractal))
          case None                => None
        }
      )
      .child(buttonSave(Entity(value = state.fractalImage)))
      .child(Button.icon(Icons.download, Actions.openSaveToDiskModal))

  // todo: this ignores the current state of fractalImage ...
  def buttonGoToDetails(fractal: WithId[FractalImageEntity])(implicit state: ExplorerState, update: NutriaState => Unit) =
    Link(Links.detailsState(fractal, state.user))
      .classes("button", "is-rounded")
      .child(Icons.icon(Icons.edit))

  def buttonSave(fractalImage: FractalImageEntity)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button
      .icon(Icons.snapshot, Actions.saveAsNewFractal(fractalImage))
      .classes("is-primary", "is-rounded")

  def saveDialog()(implicit state: ExplorerState, update: NutriaState => Unit): Option[Node] =
    state.saveModal.map { params =>
      val lensParams     = ExplorerState.saveModal.composeLens(LenseUtils.unsafe(monocle.std.all.some[SaveFractalDialog]))
      val downloadAction = Actions.saveToDisk(state.fractalImage.copy(antiAliase = params.antiAliase), params.dimensions)

      Modal(closeAction = Actions.closeSaveToDiskModal)(
        Node("div")
          .style("marginBottom", "1.5rem")
          .child(Node("h1.title").text("Render high resolution Image"))
          .child(
            Form.forLens(
              "width",
              lensParams composeLens SaveFractalDialog.dimensions composeLens Dimensions.width
            )
          )
          .child(
            Form.forLens(
              "height",
              lensParams composeLens SaveFractalDialog.dimensions composeLens Dimensions.height
            )
          )
          .child(Form.forLens("anti alias", lensParams composeLens SaveFractalDialog.antiAliase)),
        ButtonList(Button("Download", Icons.download, downloadAction).classes("is-primary"))
      )
    }
}
