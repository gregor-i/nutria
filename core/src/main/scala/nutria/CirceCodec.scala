package nutria

import eu.timepit.refined.api.{RefType, Validate}
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}

import scala.language.higherKinds

trait CirceCodec {
  val semiauto: io.circe.generic.extras.semiauto.type = io.circe.generic.extras.semiauto

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  implicit final def refinedDecoder[T, P, F[_, _]](
      implicit
      underlying: Decoder[T],
      validate: Validate[T, P],
      refType: RefType[F]
  ): Decoder[F[T, P]] =
    io.circe.refined.refinedDecoder

  implicit final def refinedEncoder[T, P, F[_, _]](
      implicit
      underlying: Encoder[T],
      refType: RefType[F]
  ): Encoder[F[T, P]] =
    io.circe.refined.refinedEncoder
}
