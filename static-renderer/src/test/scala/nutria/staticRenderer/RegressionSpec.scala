package nutria.staticRenderer

import nutria.core.FractalImage
import nutria.core.viewport.Dimensions
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}

import scala.util.Try

class RegressionSpec extends RenderingSuite {
  val cases = Seq[(String, String)]()

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
