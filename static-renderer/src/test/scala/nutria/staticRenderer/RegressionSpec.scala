package nutria.staticRenderer

import nutria.core.FractalImage
import nutria.core.viewport.Dimensions
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}

import scala.util.Try

class RegressionSpec extends RenderingSuite {
  val cases =
    Seq(
      "newton-smoothing" -> "eyJwcm9ncmFtIjp7Ik5ld3Rvbkl0ZXJhdGlvbiI6eyJtYXhJdGVyYXRpb25zIjoyMDAsInRocmVzaG9sZCI6MC4wMDAxLCJmdW5jdGlvbiI6InheMyAtIDEiLCJpbml0aWFsIjoibGFtYmRhIiwiY2VudGVyIjpbMCwwXSwiYnJpZ2h0bmVzc0ZhY3RvciI6MjUsIm92ZXJzaG9vdCI6MX19LCJ2aWV3IjpbLTIuODAyNTUwMzI5MDIzMzU2LC0yLjQ0ODkwMjI0NTIwMTk5MDYsMy41MzUwNjE2NjkxNzUwMTY1LDEuMjAzMjY0NzQ3NTA2NzM2NiwtMC41NTMxNjc3NjE3MDQwNDkxLDEuNjI1MTQ3MDQ2ODc4Mjg4XSwiYW50aUFsaWFzZSI6MX0="
    )

  def decode(encoded: String) =
    (for {
      decoded <- Try(java.util.Base64.getDecoder.decode(encoded)).toEither
      json    <- io.circe.parser.parse(new String(decoded))
      decoded <- json.as[FractalImage]
    } yield decoded).toOption

  for {
    (name, encoded) <- cases
  } renderingTest(name)(
    fractal = decode(encoded).get,
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/${name}.png"
  )
}
