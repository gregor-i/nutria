package nutria.staticRenderer

import nutria.core.FractalImage
import nutria.core.viewport.Dimensions
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Try

class RegressionSpec extends AnyFunSuite with RenderingSuite {
  val cases =
    Seq(
      "newton-smoothing" -> "eyJwcm9ncmFtIjp7Ik5ld3Rvbkl0ZXJhdGlvbiI6eyJtYXhJdGVyYXRpb25zIjoyMDAsInRocmVzaG9sZCI6MC4wMDAxLCJmdW5jdGlvbiI6InheMyAtIDEiLCJpbml0aWFsIjoibGFtYmRhIiwiY2VudGVyIjpbMCwwXSwiYnJpZ2h0bmVzc0ZhY3RvciI6MjUsIm92ZXJzaG9vdCI6MX19LCJ2aWV3IjpbLTEuMzMyMDU0Njg0MjUyNDg5NiwtMC43MDAyODgxNjg0MzkxMTk1LDAuNzE5MzA5ODAxMDk4MTYyOSwwLjI0NDgzODc2Mjk5NjU5ODgyLC0wLjEwODk4Nzc4NzkxNjk4MTMxLDAuMzIwMTk0MzMxNTIzMzIwM10sImFudGlBbGlhc2UiOjF9",
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
