package nutria.frontend

import nutria.core.Viewport
import nutria.core.viewport.Dimensions
import nutria.data.Defaults
import nutria.frontend.shaderBuilder.{Iteration, MandelbrotIteration}

case class State(dim: Dimensions,
                 view: Viewport,
                 maxIterations: Int,
                 escapeRadius: Double,
                 antiAliase: Int,
                 shaded: Boolean,
                 iteration: Iteration)

object State {
  def initial = State(
    dim = Defaults.defaultDimensions.scale(0.25),
    view = Defaults.defaultViewport,
    maxIterations = 200,
    escapeRadius = 100.1 * 100,
    antiAliase = 2,
    shaded = true,
    iteration = MandelbrotIteration
  )
}
