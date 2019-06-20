package nutria.data

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class FractalEntity(description: String,
                         reference: Option[String],
                         program: FractalProgram)


object FractalEntity {
  implicit val decoder: Decoder[FractalEntity] = deriveDecoder
  implicit val encoder: Encoder[FractalEntity] = deriveEncoder
}


