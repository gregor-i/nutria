package nutria.frontend

import nutria.core.Viewport
import nutria.core.viewport.Dimensions
import nutria.data.Defaults

case class State(dim: Dimensions,
                 view: Viewport,
                 maxIterations: Int,
                 escapeRadius: Double,
                 antiAliase: Int)

object State {
  def initial = State(
    dim = Defaults.defaultDimensions.scale(0.25),
    view = Defaults.defaultViewport,
    maxIterations = 200,
    escapeRadius = 100.1 * 100,
    antiAliase = 2
  )
}
