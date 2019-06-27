package controller


import io.circe.syntax._
import javax.inject.Inject
import nutria.data._
import play.api.mvc.InjectedController

class FractalController @Inject()() extends InjectedController with CirceSupport {
  val fractals = Vector[FractalProgram](
    Mandelbrot(),
    Mandelbrot(shaded = false),
    JuliaSet(c = (-0.6, 0.6)),
    JuliaSet(c = (-0.6, 0.6), shaded = false),
    NewtonIteration(function = "x*x*x - 1", initial = "lambda"),
    NewtonIteration(function = "x*x*x -x - 1", initial = "lambda"),
    NewtonIteration(function = "x*x*x + 1/x - 1", initial = "lambda"),
    NewtonIteration(function = "(x * x + lambda - 1) * x - lambda", initial = "0"),
  )

  def savedFractals() = Action(
    Ok(fractals.asJson)
  )
}