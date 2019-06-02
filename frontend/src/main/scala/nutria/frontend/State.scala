package nutria.frontend

import nutria.core.Viewport
import nutria.core.viewport.Dimensions
import nutria.data.Defaults

case class State(dim: Dimensions,
                 view: Viewport)

object State {
  def initial = State(
    dim = Defaults.defaultDimensions.scale(0.25),
    view = Defaults.defaultViewport
  )
}