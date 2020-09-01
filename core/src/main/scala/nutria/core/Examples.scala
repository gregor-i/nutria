package nutria.core

import nutria.core.languages.StringFunction
import nutria.macros.StaticContent

object Examples {
  val timeEscape = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/time_escape.glsl"),
    parameters = Vector(
      IntParameter("max_iterations", value = 200),
      FloatParameter("escape_radius", value = 100.0),
      RGBAParameter("color_inside", value = RGB.white.withAlpha()),
      RGBAParameter("color_outside", value = RGB.black.withAlpha()),
      InitialFunctionParameter("initial", value = StringFunction.unsafe("0")),
      FunctionParameter("iteration", value = StringFunction.unsafe("z*z+lambda"))
    ),
    exampleViewport = Viewport.mandelbrot
  )

  val gaussianInteger = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/gaussian_integer.glsl"),
    parameters = Vector(
      IntParameter("max_iterations", value = 200),
      FloatParameter("escape_radius", value = 100.0),
      RGBAParameter("color_near_gaussian", value = RGB.white.withAlpha()),
      RGBAParameter("color_far_gaussian", value = RGB.black.withAlpha()),
      InitialFunctionParameter("initial", value = StringFunction.unsafe("lambda")),
      FunctionParameter("iteration", value = StringFunction.unsafe("z*z+lambda"))
    ),
    exampleViewport = Viewport.mandelbrot
  )

  val normalMap =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/normal_map.glsl"),
      parameters = Vector(
        IntParameter("max_iterations", value = 200),
        FloatParameter("escape_radius", value = 100.0),
        FloatParameter("angle", value = 45.0 * Math.PI / 180.0),
        RGBAParameter("color_inside", value = RGB(0.0, 0.0, 255.0 / 4.0).withAlpha()),
        RGBAParameter("color_light", value = RGB.white.withAlpha()),
        RGBAParameter("color_shadow", value = RGB.black.withAlpha()),
        FloatParameter("h2", value = 2.0),
        InitialFunctionParameter("initial", value = StringFunction.unsafe("0"), includeDerivative = true),
        FunctionParameter("iteration", value = StringFunction.unsafe("z*z+lambda"), includeDerivative = true)
      ),
      exampleViewport = Viewport.mandelbrot
    )

  val outerDistance =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/outer_distance.glsl"),
      parameters = Vector(
        IntParameter("max_iterations", value = 200),
        FloatParameter("escape_radius", value = 100.0),
        FloatParameter("distance_factor", value = 1f),
        RGBAParameter("color_inside", value = RGB(0.0, 0.0, 255.0 / 4.0).withAlpha()),
        RGBAParameter("color_far", value = RGB.white.withAlpha()),
        RGBAParameter("color_near", value = RGB.black.withAlpha()),
        InitialFunctionParameter("initial", value = StringFunction.unsafe("0"), includeDerivative = true),
        FunctionParameter("iteration", value = StringFunction.unsafe("z*z+lambda"), includeDerivative = true)
      ),
      exampleViewport = Viewport.mandelbrot
    )

  val newtonIteration =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/newton_iteration.glsl"),
      parameters = Vector(
        FloatParameter("threshold", value = 1e-4),
        FloatParameter("overshoot", value = 1.0),
        FloatParameter("brightness_factor", value = 25.0),
        FloatParameter("center_x", value = 0.0),
        FloatParameter("center_y", value = 0.0),
        IntParameter("max_iterations", value = 200),
        InitialFunctionParameter("initial", value = StringFunction.unsafe("lambda")),
        NewtonFunctionParameter("f", value = StringFunction.unsafe("z*z*z + 1"), includeDerivative = true)
      ),
      exampleViewport = Viewport.aroundZero
    )

  val sierpinskiTriangle =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/sierpinsky_triangle.glsl"),
      parameters = Vector(
        IntParameter(name = "iterations", value = 25),
        FloatParameter(name = "size", value = 1.0),
        RGBAParameter(name = "color_outside", value = RGB.white.withAlpha()),
        RGBAParameter(name = "color_inside", value = RGB.black.withAlpha())
      ),
      exampleViewport = Viewport.aroundZero
    )

  val lyapunovFractal =
    FractalTemplate(
      code = StaticContent("shader-builder/src/main/glsl/lyapunov_fractal.glsl"),
      parameters = Vector(
        IntParameter(name = "iterations", value = 120),
        IntParameter(name = "steps_X", value = 6),
        IntParameter(name = "steps_Y", value = 6)
      ),
      exampleViewport = Viewport.aroundZero
    )

  val kochSnowflake = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/koch_snowflake.glsl"),
    parameters = Vector(
      IntParameter(name = "iterations", value = 26),
      RGBAParameter(name = "color_inside", value = RGB.black.withAlpha()),
      RGBAParameter(name = "color_outside", value = RGB.white.withAlpha())
    ),
    exampleViewport = Viewport.aroundZero
  )

  val novaFractal = FractalTemplate(
    code = StaticContent("shader-builder/src/main/glsl/nova_fractal.glsl"),
    parameters = Vector(
      IntParameter(name = "max_iterations", value = 200),
      NewtonFunctionParameter(name = "iteration", value = StringFunction.unsafe("z*z*z - 1"), includeDerivative = true)
    ),
    exampleViewport = Viewport.aroundZero
  )

  val allNamed: Seq[(String, FractalTemplate)] = Seq(
    ("timeEscape", timeEscape),
    ("gaussianInteger", gaussianInteger),
    ("normalMap", normalMap),
    ("outerDistance", outerDistance),
    ("newtonIteration", newtonIteration),
    ("sierpinskiTriangle", sierpinskiTriangle),
    ("lyapunovFractal", lyapunovFractal),
    ("kochSnowflake", kochSnowflake),
    ("novaFractal", novaFractal)
  )

  val all: Seq[FractalTemplate] = allNamed.map(_._2)
}
