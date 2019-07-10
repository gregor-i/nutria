package nutria.core

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import monocle.Prism
import monocle.macros.GenPrism


sealed trait FractalProgram {
  def view: Viewport
  def antiAliase: Int
  def withViewport(viewport: Viewport): FractalProgram
}


@monocle.macros.Lenses()
case class Mandelbrot(view: Viewport = DefaultViewport.defaultViewport,
                      antiAliase: Int = 2,
                      maxIterations: Int = 200,
                      escapeRadius: Double = 100,
                      shaded: Boolean = true) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

@monocle.macros.Lenses()
case class JuliaSet(view: Viewport = DefaultViewport.defaultViewport,
                    antiAliase: Int = 2,
                    maxIterations: Int = 200,
                    escapeRadius: Double = 100,
                    c: (Double, Double) = (-0.6, 0.6),
                    shaded: Boolean = true) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

@monocle.macros.Lenses()
case class TricornIteration(view: Viewport = DefaultViewport.defaultViewport,
                            antiAliase: Int = 2,
                            maxIterations: Int = 200,
                            escapeRadius: Double = 100) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

@monocle.macros.Lenses()
case class NewtonIteration(view: Viewport = DefaultViewport.defaultViewport,
                           antiAliase: Int = 2,
                           maxIterations: Int = 200,
                           threshold: Double = 1e-4,
                           function: String = "x*x*x - 1",
                           initial: String = "lambda",
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
  val newtonIteration: Prism[FractalProgram, NewtonIteration] = GenPrism[FractalProgram, NewtonIteration]

  implicit val encodeViewport: Encoder[Viewport] = deriveEncoder
  implicit val decodeViewport: Decoder[Viewport] = deriveDecoder

  implicit val decoder: Decoder[FractalProgram] = deriveDecoder
  implicit val encoder: Encoder[FractalProgram] = deriveEncoder
}


