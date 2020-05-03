package nutria.core

import nutria.macros.StaticContent

object ToFreestyle {
  def apply(fractalProgram: FractalProgram): FreestyleProgram =
    fractalProgram match {
      case f: FreestyleProgram => f
      case n: NewtonIteration =>
        FreestyleProgram(
          code = StaticContent("shader-builder/src/main/glsl/newton_iteration.glsl"),
          parameters = Seq(
            FloatParameter("threshold", n.threshold.value.toFloat),
            FloatParameter("overshoot", n.overshoot.value.toFloat),
            FloatParameter("brightness_factor", n.brightnessFactor.value.toFloat),
            FloatParameter("center_x", n.center._1.toFloat),
            FloatParameter("center_y", n.center._2.toFloat),
            IntParameter("max_iterations", n.maxIterations.value),
            InitialFunctionParameter("initial", n.initial),
            NewtonFunctionParameter("f", n.function, includeDerivative = true)
          )
        )

      case n: DivergingSeries if n.coloring.isInstanceOf[TimeEscape] =>
        val c = n.coloring.asInstanceOf[TimeEscape]
        FreestyleProgram(
          code = StaticContent("shader-builder/src/main/glsl/time_escape.glsl"),
          parameters = Seq(
            IntParameter("max_iterations", n.maxIterations.value),
            FloatParameter("escape_radius", n.escapeRadius.value.toFloat),
            RGBAParameter("color_inside", c.colorInside),
            RGBAParameter("color_outside", c.colorOutside),
            InitialFunctionParameter("initial", n.initial),
            FunctionParameter("iteration", n.iteration)
          )
        )

      case n: DivergingSeries if n.coloring.isInstanceOf[NormalMap] =>
        val c = n.coloring.asInstanceOf[NormalMap]
        FreestyleProgram(
          code = StaticContent("shader-builder/src/main/glsl/normal_map.glsl"),
          parameters = Seq(
            IntParameter("max_iterations", n.maxIterations.value),
            FloatParameter("escape_radius", n.escapeRadius.value.toFloat),
            FloatParameter("angle", c.angle.value.toFloat),
            RGBAParameter("color_inside", c.colorInside),
            RGBAParameter("color_light", c.colorLight),
            RGBAParameter("color_shadow", c.colorShadow),
            FloatParameter("h2", c.h2.value.toFloat),
            InitialFunctionParameter("initial", n.initial, includeDerivative = true),
            FunctionParameter("iteration", n.iteration, includeDerivative = true)
          )
        )

      case n: DivergingSeries if n.coloring.isInstanceOf[OuterDistance] =>
        val c = n.coloring.asInstanceOf[OuterDistance]
        FreestyleProgram(
          code = StaticContent("shader-builder/src/main/glsl/outer_distance.glsl"),
          parameters = Seq(
            IntParameter("max_iterations", n.maxIterations.value),
            FloatParameter("escape_radius", n.escapeRadius.value.toFloat),
            FloatParameter("distance_factor", c.distanceFactor.value.toFloat),
            RGBAParameter("color_inside", c.colorInside),
            RGBAParameter("color_far", c.colorFar),
            RGBAParameter("color_near", c.colorNear),
            InitialFunctionParameter("initial", n.initial, includeDerivative = true),
            FunctionParameter("iteration", n.iteration, includeDerivative = true)
          )
        )
    }
}
