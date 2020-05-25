package nutria.core

import io.circe.Codec
import nutria.{CirceCodec, core}

@monocle.macros.Lenses()
case class FractalImage(
    template: FractalTemplate,
    viewport: core.Viewport,
    antiAliase: AntiAliase = 1,
    parameters: Vector[Parameter] = Vector.empty
)

object FractalImage extends CirceCodec {
  implicit val codec: Codec[FractalImage] = semiauto.deriveConfiguredCodec

  implicit val ordering: Ordering[FractalImage] = Ordering.by(_.template)
}
