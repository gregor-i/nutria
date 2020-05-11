package nutria.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import io.circe.Codec
import nutria.{CirceCodec, core}

@monocle.macros.Lenses()
case class FractalImage(
    program: FreestyleProgram,
    view: core.Viewport = Viewport.defaultViewport,
    antiAliase: Int Refined Positive = refineUnsafe(1)
)

object FractalImage extends CirceCodec {
  def allImages(fractalEntities: Seq[FractalEntity]): Seq[FractalImage] =
    for {
      entity <- fractalEntities
      view   <- entity.views.value
    } yield FractalImage(entity.program, view, entity.antiAliase)

  def firstImage(fractalEntity: FractalEntity): FractalImage =
    FractalImage(fractalEntity.program, fractalEntity.views.value.head, fractalEntity.antiAliase)

  implicit val ordering: Ordering[FractalImage] = FreestyleProgram.ordering.on(_.program)

  implicit val codec: Codec[FractalImage] = semiauto.deriveConfiguredCodec
}
