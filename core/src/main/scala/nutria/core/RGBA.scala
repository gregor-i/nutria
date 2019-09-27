package nutria.core

import io.circe.{Codec, Decoder, Encoder}

import scala.util.Try

object RGBA extends CirceCodex {
  val white = RGBA(255, 255, 255)
  val black = RGBA(0, 0, 0)

  def interpolate(la: RGBA, lb: RGBA, p: Double): RGBA = {
    require(0 <= p && p <= 1, s"$p was not in the expected interval [0, 1]")
    val q = 1 - p
    RGBA(la.R * q + lb.R * p,
      la.G * q + lb.G * p,
      la.B * q + lb.B * p,
      la.A * q + lb.A * p)
  }

  def parseRGBString(s: String): Try[RGBA] = Try {
    val i = Integer.parseInt(s.dropWhile(_ == '#'), 16)
    RGBA((i >> 16) & 0xff, (i >> 8) & 0xff, (i >> 0) & 0xff)
  }

  def toRGBString(rgba: RGBA): String =
    "#%02x%02x%02x".format(0xff & rgba.R.toInt, 0xff & rgba.G.toInt, 0xff & rgba.B.toInt)

  implicit val codec: Codec[RGBA] = Codec.from(
    encodeA = Encoder.encodeString.contramap(toRGBString),
    decodeA = Decoder.decodeString.emapTry(parseRGBString)
  )
}

final case class RGBA(R: Double, G: Double, B: Double, A: Double = 1.0) {
  require((0 <= R && R < 256) && (0 <= G && G < 256) && (0 <= B && B < 256) && (0 <= A && A <= 1), s"Requirement for RGB failed. input: R=$R, G=$G, B=$B, A=$A")
}

