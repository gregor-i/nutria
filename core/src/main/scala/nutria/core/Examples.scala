package nutria.core

import nutria.core.languages.StringFunction
import nutria.macros.StaticContent

object Examples {
  val timeEscape = FreestyleProgram(
    code = StaticContent("shader-builder/src/main/glsl/time_escape.glsl"),
    parameters = Vector(
      IntParameter("max_iterations", 200),
      FloatParameter("escape_radius", 100.0f),
      RGBAParameter("color_inside", RGB.white.withAlpha()),
      RGBAParameter("color_outside", RGB.black.withAlpha()),
      InitialFunctionParameter("initial", StringFunction.unsafe("0")),
      FunctionParameter("iteration", StringFunction.unsafe("z*z+lambda"))
    )
  )

  val normalMap =
    FreestyleProgram(
      code = StaticContent("shader-builder/src/main/glsl/normal_map.glsl"),
      parameters = Vector(
        IntParameter("max_iterations", 200),
        FloatParameter("escape_radius", 100.0f),
        FloatParameter("angle", (45.0 * Math.PI / 180.0).toFloat),
        RGBAParameter("color_inside", RGB(0.0, 0.0, 255.0 / 4.0).withAlpha()),
        RGBAParameter("color_light", RGB.white.withAlpha()),
        RGBAParameter("color_shadow", RGB.black.withAlpha()),
        FloatParameter("h2", 2.0f),
        InitialFunctionParameter("initial", StringFunction.unsafe("0"), includeDerivative = true),
        FunctionParameter("iteration", StringFunction.unsafe("z*z+lambda"), includeDerivative = true)
      )
    )

  val outerDistance =
    FreestyleProgram(
      code = StaticContent("shader-builder/src/main/glsl/outer_distance.glsl"),
      parameters = Vector(
        IntParameter("max_iterations", 200),
        FloatParameter("escape_radius", 100.0f),
        FloatParameter("distance_factor", 1f),
        RGBAParameter("color_inside", RGB(0.0, 0.0, 255.0 / 4.0).withAlpha()),
        RGBAParameter("color_far", RGB.white.withAlpha()),
        RGBAParameter("color_near", RGB.black.withAlpha()),
        InitialFunctionParameter("initial", StringFunction.unsafe("0"), includeDerivative = true),
        FunctionParameter("iteration", StringFunction.unsafe("z*z+lambda"), includeDerivative = true)
      )
    )

  val newtonIteration =
    FreestyleProgram(
      code = StaticContent("shader-builder/src/main/glsl/newton_iteration.glsl"),
      parameters = Vector(
        FloatParameter("threshold", 1e-4f),
        FloatParameter("overshoot", 1.0f),
        FloatParameter("brightness_factor", 25f),
        FloatParameter("center_x", 0f),
        FloatParameter("center_y", 0f),
        IntParameter("max_iterations", 200),
        InitialFunctionParameter("initial", StringFunction.unsafe("lambda")),
        NewtonFunctionParameter("f", StringFunction.unsafe("x*x*x + 1"), includeDerivative = true)
      )
    )

  // todo: include old freestyles

  val allNamed: Seq[(String, FreestyleProgram, Viewport)] = Seq(
    ("timeEscape", timeEscape, Viewport.mandelbrot),
    ("normalMap", normalMap, Viewport.mandelbrot),
    ("outerDistance", outerDistance, Viewport.mandelbrot),
    ("newtonIteration", newtonIteration, Viewport.aroundZero)
  )

  val all: Seq[FreestyleProgram] = allNamed.map(_._2)
}
