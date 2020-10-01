package nutria.frontend.pages.common

import monocle.Lens
import nutria.core.{FractalImage, FractalTemplate, Viewport}
import nutria.frontend.pages.explorer.ExplorerEvents
import nutria.frontend.util.Updatable
import snabbdom.Node

import scala.util.chaining._

object InteractiveFractal {
  def forImage[S](lens: Lens[S, FractalImage])(implicit updatable: Updatable[S, S]): Node =
    apply(image = lens.get(updatable.state), updatable = Updatable.composeLens(updatable, lens.composeLens(FractalImage.viewport)))

  def forTemplate[S](lens: Lens[S, FractalTemplate])(implicit updatable: Updatable[S, S]): Node =
    apply(
      image = lens.get(updatable.state).pipe(FractalImage.fromTemplate),
      updatable = Updatable.composeLens(updatable, lens.composeLens(FractalTemplate.exampleViewport))
    )

  private def apply(image: FractalImage, updatable: Updatable[Viewport, Viewport]): Node =
    Node("div.interaction-panel")
      .events(ExplorerEvents.canvasMouseEvents(updatable))
      .events(ExplorerEvents.canvasWheelEvent(updatable))
      .events(ExplorerEvents.canvasTouchEvents(updatable))
      .child(
        Node("canvas")
          .hooks(CanvasHooks(image))
      )
}
