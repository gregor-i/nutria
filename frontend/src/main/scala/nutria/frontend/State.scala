package nutria.frontend

import nutria.core.Viewport
import nutria.data.Defaults
import nutria.frontend.shaderBuilder.{Iteration, MandelbrotIteration}

case class State(view: Viewport,
                 maxIterations: Int,
                 escapeRadius: Double,
                 antiAliase: Int,
                 shaded: Boolean,
                 iteration: Iteration)

object State {
  def initial = State(
    view = Defaults.defaultViewport,
    maxIterations = 200,
    escapeRadius = 100 * 100,
    antiAliase = 2,
    shaded = true,
    iteration = MandelbrotIteration
  )
}
