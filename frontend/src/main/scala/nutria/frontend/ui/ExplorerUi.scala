package nutria.frontend.ui

import nutria.frontend._
import nutria.frontend.ui.common.{Button, CanvasHooks, Icons}
import snabbdom.Node

object ExplorerUi extends Page[ExplorerState] {
  def render(implicit state: ExplorerState, update: NutriaState => Unit) =
    Seq(common.Header(state, update), renderCanvas)

  // Actions to implement:
  //  With Fractal Id
  //    Fractal is owned by me
  //      Add Snapshot to fractal     (/)
  //    else
  //      fork and take snapshot      (/)
  //    back to fractal details
  //  return to start position
  //  render high res image and save

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

  def buttonBackToDetails(
      fractalId: String
  )(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Edit Parameters", Icons.edit, Actions.loadAndEditFractal(fractalId))

  def buttonAddViewport(
      fractalId: String
  )(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button(
      "Save this image",
      Icons.snapshot,
      Actions.addViewport(fractalId, state.fractalImage.view)
    ).classes("is-primary")

  def buttonForkAndAddViewport(
      fractalId: String
  )(implicit state: ExplorerState, update: NutriaState => Unit) =
    Button(
      "Fork and Save this image",
      Icons.copy,
      Actions.forkAndAddViewport(fractalId, state.fractalImage.view)
    ).classes("is-primary")

  def renderCanvas(implicit state: ExplorerState, update: NutriaState => Unit): Node =
    Node("div.full-size")
      .events(ExplorerEvents.canvasMouseEvents)
      .events(ExplorerEvents.canvasWheelEvent)
      .events(ExplorerEvents.canvasTouchEvents)
      .child(
        Node("canvas")
          .hooks(CanvasHooks(state.fractalImage, resize = true))
      )
      .child(renderActionBar())
}
