package nutria.core

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import monocle.macros.GenPrism
import monocle.{Lens, Prism}


sealed trait FractalProgram {
  def antiAliase: Int
}


@monocle.macros.Lenses()
case class DivergingSeries(antiAliase: Int = 2,
                           maxIterations: Int = 200,
                           escapeRadius: Double = 100,
                           initial: String, /* todo: refined types*/
                           iteration: String /* todo: refined types*/) extends FractalProgram

object DivergingSeries {
  def mandelbrot = DivergingSeries(initial = "0", iteration = "z*z + lambda")
  def juliaSet(c: Point) = DivergingSeries(initial = "lambda", iteration = s"z*z + (${c._1} + i*${c._2})", maxIterations = 50)
}

@monocle.macros.Lenses()
case class DerivedDivergingSeries(antiAliase: Int = 2,
                                  maxIterations: Int = 200,
                                  escapeRadius: Double = 100,
                                  h2: Double = 2.0,
                                  angle: Double = 45.0 / 180.0 * Math.PI,
                                  initialZ: String, /* todo: refined types*/
                                  initialZDer: String, /* todo: refined types*/
                                  iterationZ: String, /* todo: refined types*/
                                  iterationZDer: String /* todo: refined types*/) extends FractalProgram

object DerivedDivergingSeries{
  val mandelbrot = DerivedDivergingSeries(
    initialZ = "lambda",
    initialZDer = "1",
    iterationZ = "z*z + lambda",
    iterationZDer = "z'*z*2 + 1"
  )

  def juliaSet(c: Point) = DerivedDivergingSeries(
    initialZ = "lambda",
    initialZDer = "1",
    iterationZ = s"z*z + (${c._1} + i*${c._2})",
    iterationZDer = "z'*z*2 + 1"
  )
}

@monocle.macros.Lenses()
case class NewtonIteration(antiAliase: Int = 2,
                           maxIterations: Int = 200,
                           threshold: Double = 1e-4,
                           function: String = "x*x*x - 1", /* todo: refined types*/
                           initial: String = "lambda", /* todo: refined types*/
                           center: Point = (0.0, 0.0),
                           brightnessFactor: Double = 25.0,
                           overshoot: Double = 1.0
                          ) extends FractalProgram

object NewtonIteration {
  def mandelbrotPolynomial(n: Int): NewtonIteration = {
    def loop(n: Int): String =
      if (n == 1)
        "x"
      else
        s"(${loop(n - 1)})^2 + lambda"

    NewtonIteration(function = loop(n), initial = "lambda")
  }
}

@monocle.macros.Lenses()
case class FreestyleProgram(code: String,
                            antiAliase: Int = 2) extends FractalProgram

object FreestyleProgram{
  val default = FreestyleProgram("color = vec4(abs(z.x), abs(z.y), length(z), 1.0);")
}

object FractalProgram {
  val newtonIteration: Prism[FractalProgram, NewtonIteration] = GenPrism[FractalProgram, NewtonIteration]
  val divergingSeries: Prism[FractalProgram, DivergingSeries] = GenPrism[FractalProgram, DivergingSeries]
  val derivedDivergingSeries: Prism[FractalProgram, DerivedDivergingSeries] = GenPrism[FractalProgram, DerivedDivergingSeries]
  val freestyleProgram: Prism[FractalProgram, FreestyleProgram] = GenPrism[FractalProgram, FreestyleProgram]

  implicit val decoder: Decoder[FractalProgram] = deriveDecoder
  implicit val encoder: Encoder[FractalProgram] = deriveEncoder
}


