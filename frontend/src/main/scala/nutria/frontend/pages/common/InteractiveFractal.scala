package nutria.frontend.pages.common

import monocle.Lens
import nutria.core.{FractalImage, FractalTemplate, Viewport}
import nutria.frontend.GlobalState
import nutria.frontend.pages.explorer.ExplorerEvents
import nutria.frontend.util.Updatable
import snabbdom.Node

import scala.util.chaining._

object InteractiveFractal {
  def forImage[S](lens: Lens[S, FractalImage])(implicit globalState: GlobalState, updatable: Updatable[S, S]): Node =
    apply(lens.composeLens(FractalImage.viewport), lens.get(updatable.state))

  def forTemplate[S](lens: Lens[S, FractalTemplate])(implicit globalState: GlobalState, updatable: Updatable[S, S]): Node =
    apply(lens.composeLens(FractalTemplate.exampleViewport), lens.get(updatable.state).pipe(FractalImage.fromTemplate))

  private def apply[S](lensViewport: Lens[S, Viewport], image: FractalImage)(implicit globalState: GlobalState, updatable: Updatable[S, S]): Node =
    Node("div.interaction-panel")
      .events(ExplorerEvents.canvasMouseEvents(lensViewport))
      .events(ExplorerEvents.canvasWheelEvent(lensViewport))
      .events(ExplorerEvents.canvasTouchEvents(lensViewport))
      .child(
        Node("canvas")
          .hooks(CanvasHooks(image))
      )
}
