package module.auth

import io.circe.syntax._
import scala.util.chaining._

object EncodeCookie {
  val encoder: java.util.Base64.Encoder = java.util.Base64.getEncoder

  def apply[A: io.circe.Encoder](a: A): String =
    a
      .asJson.noSpaces
      .getBytes
      .pipe(encoder.encode)
      .pipe(bytes => new String(bytes))
}
