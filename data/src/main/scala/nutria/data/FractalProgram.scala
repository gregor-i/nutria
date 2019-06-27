package nutria.data

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import nutria.core.Viewport

sealed trait FractalProgram {
  def view: Viewport
  def antiAliase: Int
  def withViewport(viewport: Viewport): FractalProgram
}

case class Mandelbrot(view: Viewport = Defaults.defaultViewport,
                      antiAliase: Int = 2,
                      maxIterations: Int = 200,
                      escapeRadius: Double = 100,
                      shaded: Boolean = true) extends FractalProgram {
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

case class JuliaSet(view: Viewport = Defaults.defaultViewport,
                    antiAliase: Int = 2,
                    maxIterations: Int = 200,
                    escapeRadius: Double = 100,
                    c: (Double, Double),
                    shaded: Boolean = true) extends FractalProgram{
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

case class TricornIteration(view: Viewport = Defaults.defaultViewport,
                            antiAliase: Int = 2,
                            maxIterations: Int = 200,
                            escapeRadius: Double = 100) extends FractalProgram{
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

case class NewtonIteration(view: Viewport = Defaults.defaultViewport,
                           antiAliase: Int = 2,
                           maxIterations: Int = 200,
                           threshold: Double = 1e-6,
                           function: String,
                           initial: String
                          ) extends FractalProgram{
  def withViewport(viewport: Viewport) = copy(view = viewport)
}

object FractalProgram {
  implicit val encodeViewport: Encoder[Viewport] = deriveEncoder
  implicit val decodeViewport: Decoder[Viewport] = deriveDecoder

  implicit val decoder: Decoder[FractalProgram] = deriveDecoder
  implicit val encoder: Encoder[FractalProgram] = deriveEncoder
}


