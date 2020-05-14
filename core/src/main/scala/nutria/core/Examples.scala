package nutria.core

import nutria.core
import nutria.core.languages.StringFunction
import nutria.macros.StaticContent

object Examples {
  val timeEscape = FractalTemplate(
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

  val gaussianInteger = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/gaussian_integer.glsl"),
    parameters = Vector(
      IntParameter("max_iterations", 200),
      FloatParameter("escape_radius", 100.0f),
      RGBAParameter("color_near_gaussian", RGB.white.withAlpha()),
      RGBAParameter("color_far_gaussian", RGB.black.withAlpha()),
      InitialFunctionParameter("initial", StringFunction.unsafe("lambda")),
      FunctionParameter("iteration", StringFunction.unsafe("z*z+lambda"))
    )
  )

  val normalMap =
    FractalTemplate(
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
    FractalTemplate(
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
    FractalTemplate(
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

  val sierpinskiTriangle =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/sierpinsky_triangle.glsl"),
      parameters = Vector(
        IntParameter(name = "iterations", value = 25),
        FloatParameter(name = "size", value = 1.0.toFloat),
        RGBParameter(name = "color_outside", value = RGB.white),
        RGBParameter(name = "color_inside", value = RGB.black)
      )
    )

  val lyapunovFractal =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/lyapunov_fractal.glsl"),
      parameters = Vector(
        IntParameter(name = "iterations", value = 120),
        IntParameter(name = "steps_X", value = 6),
        IntParameter(name = "steps_Y", value = 6)
      )
    )

  val kochSnowflake = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/koch_snowflake.glsl"),
    parameters = Vector(
      IntParameter(name = "iterations", value = 26),
      RGBAParameter(name = "color_inside", value = RGB.black.withAlpha()),
      RGBAParameter(name = "color_outside", value = RGB.white.withAlpha())
    )
  )

  val novaFractal = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/nova_fractal.glsl"),
    parameters = Vector(
      IntParameter(name = "max_iterations", value = 200),
      NewtonFunctionParameter(name = "iteration", value = StringFunction.unsafe("x*x*x - 1"), includeDerivative = true)
    )
  )

  val allNamed: Seq[(String, FractalTemplate, core.Viewport)] = Seq(
    ("timeEscape", timeEscape, Viewport.mandelbrot),
    ("gaussianInteger", gaussianInteger, Viewport.mandelbrot),
    ("normalMap", normalMap, Viewport.mandelbrot),
    ("outerDistance", outerDistance, Viewport.mandelbrot),
    ("newtonIteration", newtonIteration, Viewport.aroundZero),
    ("sierpinskiTriangle", sierpinskiTriangle, Viewport.aroundZero),
    ("lyapunovFractal", lyapunovFractal, Viewport.aroundZero),
    ("kochSnowflake", kochSnowflake, Viewport.aroundZero),
    ("novaFractal", novaFractal, Viewport.aroundZero)
  )

  val all: Seq[FractalTemplate] = allNamed.map(_._2)
}
