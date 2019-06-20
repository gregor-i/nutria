package nutria.frontend

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import nutria.frontend.shaderBuilder.FractalProgram

case class ViewerState(fractalProgram: FractalProgram = FractalProgram(),
                       dragStartPosition: Option[(Double, Double)] = None)

object ViewerState {
  implicit val decoder: Decoder[ViewerState] = deriveDecoder
  implicit val encoder: Encoder[ViewerState] = deriveEncoder
}
