package module.auth

import io.circe.parser.parse

import scala.util.chaining._

object DecodeCookie {
  val decoder: java.util.Base64.Decoder = java.util.Base64.getDecoder

  def apply[A: io.circe.Decoder](value: String): Option[A] =
    value
      .pipe(decoder.decode)
      .pipe(new String(_))
      .pipe(parse)
      .flatMap(_.as[A])
      .toOption
}
