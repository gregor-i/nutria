package nutria.frontend.ui

import nutria.frontend._
import nutria.frontend.ui.common.{Button, CanvasHooks, Icons}
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
        state.fractalId match {
          case Some(fractalId) if state.owned => Some(buttonAddViewport(fractalId))
          case Some(fractalId)                => Some(buttonForkAndAddViewport(fractalId))
          case None                           => None
        }
      )
      .childOptional(
        state.fractalId match {
          case Some(fractalId) => Some(buttonBackToDetails(fractalId))
          case None            => None
        }
      )

  def buttonBackToDetails(fractalId: String)(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Edit Parameters", Icons.edit, Actions.loadAndEditFractal(fractalId))

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
