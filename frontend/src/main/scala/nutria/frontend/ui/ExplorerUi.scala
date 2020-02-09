package nutria.frontend.ui

import nutria.core.{FractalEntityWithId, User}
import nutria.frontend._
import nutria.frontend.ui.common.{Button, CanvasHooks, Icons, Link}
import nutria.frontend.ui.explorer.ExplorerEvents
import snabbdom.Node

object ExplorerUi extends Page[ExplorerState] {
  def render(implicit state: ExplorerState, update: NutriaState => Unit) =
    Seq(
      common.Header(state, update),
      renderCanvas
        .child(renderActionBar())
    )

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

  def buttonBackToDetails(fractal: FractalEntityWithId)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Link(Links.detailsState(fractal, state.user))
      .classes("button")
      .child(Icons.icon(Icons.edit))
      .child(Node("span").text("Edit Parameters"))

  def buttonAddViewport(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Save this image", Icons.snapshot, Actions.addViewport(fractalId, state.fractalImage.view)).classes("is-primary")

  def buttonForkAndAddViewport(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Fork and Save this image", Icons.copy, Actions.forkAndAddViewport(fractalId, state.fractalImage.view)).classes("is-primary")

  def renderCanvas(implicit state: ExplorerState, update: NutriaState => Unit): Node =
    Node("div.interation-panel")
      .events(ExplorerEvents.canvasMouseEvents)
      .events(ExplorerEvents.canvasWheelEvent)
      .events(ExplorerEvents.canvasTouchEvents)
      .child(
        Node("canvas")
          .hooks(CanvasHooks(state.fractalImage, resize = true))
      )
}
