package nutria.frontend.ui

import nutria.core.viewport.Dimensions
import nutria.core.{FractalEntityWithId, User}
import nutria.frontend._
import nutria.frontend.ui.common._
import nutria.frontend.ui.explorer.ExplorerEvents
import nutria.frontend.util.LenseUtils
import snabbdom.Node

object ExplorerUi extends Page[ExplorerState] {
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

  def buttonBackToDetails(fractal: FractalEntityWithId)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Link(Links.detailsState(fractal, state.user))
      .classes("button", "is-rounded")
      .child(Icons.icon(Icons.edit))

  def buttonAddViewport(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button
      .icon(Icons.snapshot, Actions.addViewport(fractalId, state.fractalImage.view))
      .classes("is-primary", "is-rounded")

  def buttonForkAndAddViewport(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Fork and Save this image", Icons.copy, Actions.forkAndAddViewport(fractalId, state.fractalImage.view))
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
      val lensParams     = LenseUtils.subclass(ExplorerState.saveModal, monocle.std.all.some[SaveFractalDialog], params)
      val downloadAction = Actions.saveToDisk(state.fractalImage.copy(antiAliase = params.antiAliase), params.dimensions)

      Node("div.modal.is-active")
        .children(
          Node("div.modal-background").event("click", Actions.closeSaveToDiskModal),
          Node("div.modal-content")
            .child(
              Node("div.box")
                .child(
                  Node("div")
                    .style("marginBottom", "1.5rem")
                    .child(Node("h1.title").text("Render high resolution Image"))
                    .child(
                      Form.intInput(
                        "width",
                        lensParams composeLens SaveFractalDialog.dimensions composeLens Dimensions.width
                      )
                    )
                    .child(
                      Form.intInput(
                        "height",
                        lensParams composeLens SaveFractalDialog.dimensions composeLens Dimensions.height
                      )
                    )
                    .child(Form.intInput("anti alias", lensParams composeLens SaveFractalDialog.antiAliase))
                )
                .children(
                  Button
                    .list()
                    .child(
                      Button("Download", Icons.download, downloadAction)
                        .classes("is-primary")
                    )
                )
            )
        )
    }
}
