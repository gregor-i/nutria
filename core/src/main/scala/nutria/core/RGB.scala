package nutria.core

import io.circe.{Codec, Decoder, Encoder}

import scala.util.Try

object RGB extends CirceCodec {
  val white = RGB(255, 255, 255)
  val black = RGB(0, 0, 0)

  def interpolate(la: RGB, lb: RGB, p: Double): RGB = {
    require(0 <= p && p <= 1, s"$p was not in the expected interval [0, 1]")
    val q = 1 - p
    RGB(la.R * q + lb.R * p, la.G * q + lb.G * p, la.B * q + lb.B * p)
  }

  def parseRGBString(s: String): Try[RGB] = Try {
    val i = Integer.parseInt(s.dropWhile(_ == '#'), 16)
    RGB((i >> 16) & 0xff, (i >> 8) & 0xff, (i >> 0) & 0xff)
  }

  def toRGBString(rgba: RGB): String =
    "#%02x%02x%02x".format(0xff & rgba.R.toInt, 0xff & rgba.G.toInt, 0xff & rgba.B.toInt)

  implicit val codec: Codec[RGB] = Codec.from(
    encodeA = Encoder.encodeString.contramap(toRGBString),
    decodeA = Decoder.decodeString.emapTry(parseRGBString)
  )
}

final case class RGB(R: Double, G: Double, B: Double) {
  require(
    (0 <= R && R < 256) && (0 <= G && G < 256) && (0 <= B && B < 256),
    s"Requirement for RGB failed. input: R=$R, G=$G, B=$B"
  )

  def withAlpha(A: Double = 1.0): RGBA =
    RGBA(R, G, B, A)
}
