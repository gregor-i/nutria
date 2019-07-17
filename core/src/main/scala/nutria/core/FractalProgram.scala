package nutria.core

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import monocle.macros.GenPrism
import monocle.{Lens, Prism}


sealed trait FractalProgram {
  def view: Viewport
  def antiAliase: Int
  def withViewport(viewport: Viewport): FractalProgram
}


@monocle.macros.Lenses()
case class DivergingSeries(view: Viewport = DefaultViewport.defaultViewport,
                           antiAliase: Int = 2,
                           maxIterations: Int = 200,
                           escapeRadius: Double = 100,
                           initial: String, /* todo: refined types*/
                           iteration: String /* todo: refined types*/) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

object DivergingSeries {
  def mandelbrot = DivergingSeries(initial = "0", iteration = "z*z + lambda")
  def juliaSet(c: Point) = DivergingSeries(initial = "0", iteration = s"z*z + (${c._1} + i*${c._2})")
}

@monocle.macros.Lenses()
case class DerivedDivergingSeries(view: Viewport = DefaultViewport.defaultViewport,
                                  antiAliase: Int = 2,
                                  maxIterations: Int = 200,
                                  escapeRadius: Double = 100,
                                  h2: Double = 2.0,
                                  angle: Double = 45.0 / 180.0 * Math.PI,
                                  initialZ: String, /* todo: refined types*/
                                  initialZDer: String, /* todo: refined types*/
                                  iterationZ: String, /* todo: refined types*/
                                  iterationZDer: String /* todo: refined types*/) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

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
case class TricornIteration(view: Viewport = DefaultViewport.defaultViewport,
                            antiAliase: Int = 2,
                            maxIterations: Int = 200,
                            escapeRadius: Double = 100) /*extends FractalProgram*/ {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

@monocle.macros.Lenses()
case class NewtonIteration(view: Viewport = DefaultViewport.defaultViewport,
                           antiAliase: Int = 2,
                           maxIterations: Int = 200,
                           threshold: Double = 1e-4,
                           function: String = "x*x*x - 1", /* todo: refined types*/
                           initial: String = "lambda", /* todo: refined types*/
                           center: Point = (0.0, 0.0),
                           brightnessFactor: Double = 25.0,
                           overshoot: Double = 1.0
                          ) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

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

object FractalProgram {
  val viewport: Lens[FractalProgram, Viewport] = Lens[FractalProgram, Viewport](_.view)(view => _.withViewport(view))

  val newtonIteration: Prism[FractalProgram, NewtonIteration] = GenPrism[FractalProgram, NewtonIteration]
  val divergingSeries: Prism[FractalProgram, DivergingSeries] = GenPrism[FractalProgram, DivergingSeries]
  val derivedDivergingSeries: Prism[FractalProgram, DerivedDivergingSeries] = GenPrism[FractalProgram, DerivedDivergingSeries]

  implicit val encodeViewport: Encoder[Viewport] = deriveEncoder
  implicit val decodeViewport: Decoder[Viewport] = deriveDecoder

  implicit val decoder: Decoder[FractalProgram] = deriveDecoder
  implicit val encoder: Encoder[FractalProgram] = deriveEncoder
}


