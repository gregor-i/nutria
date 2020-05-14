package nutria.core

import io.circe.Codec
import nutria.{CirceCodec, core}

@monocle.macros.Lenses()
case class FractalImage(
    template: FractalTemplate,
    viewport: core.Viewport,
    antiAliase: AntiAliase = refineUnsafe(1),
    parameters: Vector[Parameter] = Vector.empty
)

object FractalImage extends CirceCodec {
  def allImages(fractalEntities: Seq[FractalEntity]): Seq[FractalImage] =
    for {
      entity <- fractalEntities
      view   <- entity.views.value
    } yield FractalImage(entity.program, view, entity.antiAliase)

  def firstImage(fractalEntity: FractalEntity): FractalImage =
    FractalImage(fractalEntity.program, fractalEntity.views.value.head, fractalEntity.antiAliase)

  implicit val codec: Codec[FractalImage] = semiauto.deriveConfiguredCodec
}
