package nutria.core

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import io.circe.Codec
import nutria.core.viewport.Viewport
@monocle.macros.Lenses()
case class FractalImage(
    program: FractalProgram,
    view: Viewport = Viewport.defaultViewport,
    antiAliase: Int Refined Positive = refineMV(1)
)

object FractalImage extends CirceCodex {
  def allImages(fractalEntities: Seq[FractalEntity]): Seq[FractalImage] =
    for {
      entity <- fractalEntities
      view   <- entity.views.value
    } yield FractalImage(entity.program, view, entity.antiAliase)

  def firstImage(fractalEntity: FractalEntity): FractalImage =
    FractalImage(fractalEntity.program, fractalEntity.views.value.head, fractalEntity.antiAliase)

  implicit val ordering: Ordering[FractalImage] = FractalProgram.ordering.on(_.program)

  implicit val codec: Codec[FractalImage] = semiauto.deriveConfiguredCodec
}
