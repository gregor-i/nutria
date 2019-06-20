package nutria.frontend.shaderBuilder

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import nutria.core.Viewport
import nutria.data.Defaults
import spire.math.Complex

import scala.scalajs.js.URIUtils

case class FractalProgram(view: Viewport = Defaults.defaultViewport,
                          maxIterations: Int = 200,
                          escapeRadius: Double = 100,
                          antiAliase: Int = 2,
                          shaded: Boolean = true,
                          iteration: Iteration = MandelbrotIteration)

object FractalProgram {
  implicit val encodeViewport: Encoder[Viewport] = deriveEncoder
  implicit val decodeViewport: Decoder[Viewport] = deriveDecoder

  implicit val encodeIteration: Encoder[Iteration] = deriveEncoder
  implicit val decodeIteration: Decoder[Iteration] = deriveDecoder

  implicit val encodeComplex: Encoder[Complex[Double]] = implicitly[Encoder[(Double, Double)]].contramap(c => (c.real, c.imag))
  implicit val decodeComplex: Decoder[Complex[Double]] = implicitly[Decoder[(Double, Double)]].map(t => Complex(t._1, t._2))

  implicit val decoder: Decoder[FractalProgram] = deriveDecoder
  implicit val encoder: Encoder[FractalProgram] = deriveEncoder

  def queryEncoded(fractalProgram: FractalProgram): String = URIUtils.encodeURIComponent(fractalProgram.asJson.noSpaces)
  def queryDecoded(string: String): Option[FractalProgram] =
    parser.parse(URIUtils.decodeURIComponent(string))
      .flatMap(_.as[FractalProgram])
      .toOption
}


