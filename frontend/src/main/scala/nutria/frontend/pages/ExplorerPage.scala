package nutria.frontend.pages

import monocle.Lens
import monocle.macros.Lenses
import nutria.api.{FractalEntity, User, WithId}
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
    remoteFractal: Option[WithId[FractalEntity]],
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

  override def stateFromUrl: PartialFunction[(Path, QueryParameter), NutriaState] = {
    case (s"/fractals/${fractalId}/explorer", queryParams) =>
      LoadingState(
        for {
          user          <- NutriaService.whoAmI()
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
            case None => ErrorState("Query Parameter is invalid")
          }
        }
      )
    case ("/explorer", queryParams) =>
      LoadingState(
        NutriaService
          .whoAmI()
          .map { user =>
            val fractalFromUrl =
              queryParams.get("state").flatMap(Router.queryDecoded[FractalImage])

            fractalFromUrl match {
              case Some(fractal) => ExplorerState(user, None, fractal)
              case None          => ErrorState("Query Parameter is invalid")
            }
          }
      )

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
      .child(renderCanvas)
      .child(renderActionBar())
      .childOptional(saveDialog())

  def renderActionBar()(implicit state: ExplorerState, update: NutriaState => Unit): Node =
    Node("div.buttons.overlay-bottom-right.padding")
      .childOptional(
        state.remoteFractal match {
          case Some(fractal) if User.isOwner(state.user, fractal) => Some(buttonAddViewport(fractal.id))
          case Some(fractal)                                      => Some(buttonForkAndAddViewport(fractal.id))
          case None                                               => None
        }
      )
      .childOptional(
        state.remoteFractal match {
          case Some(fractal) => Some(buttonBackToDetails(fractal))
          case None          => None
        }
      )
      .child(
        Button.icon(Icons.download, Actions.openSaveToDiskModal)
      )

  def buttonBackToDetails(fractal: WithId[FractalEntity])(implicit state: ExplorerState, update: NutriaState => Unit) =
    Link(Links.detailsState(fractal, state.user))
      .classes("button", "is-rounded")
      .child(Icons.icon(Icons.edit))

  def buttonAddViewport(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button
      .icon(Icons.snapshot, Actions.addViewport(fractalId, state.fractalImage.viewport))
      .classes("is-primary", "is-rounded")

  def buttonForkAndAddViewport(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Fork and Save this image", Icons.copy, Actions.forkAndAddViewport(fractalId, state.fractalImage.viewport))
      .classes("is-primary")

  def renderCanvas(implicit state: ExplorerState, update: NutriaState => Unit): Node =
    Node("div.interaction-panel")
      .events(ExplorerEvents.canvasMouseEvents)
      .events(ExplorerEvents.canvasWheelEvent)
      .events(ExplorerEvents.canvasTouchEvents)
      .child(
        Node("canvas")
          .hooks(CanvasHooks(state.fractalImage, resize = true))
      )

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
