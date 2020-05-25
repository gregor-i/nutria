package nutria.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{NonNegative, Positive}
import io.circe.Codec
import nutria.CirceCodec

@deprecated
@monocle.macros.Lenses()
case class Fractal(
    program: FractalTemplate,
    views: ViewportList = ViewportList.refineUnsafe(List(Viewport.aroundZero)),
    antiAliase: Int Refined Positive = refineUnsafe(1),
    upvotes: Int Refined NonNegative = refineUnsafe(0),
    downvotes: Int Refined NonNegative = refineUnsafe(0)
)

object Fractal extends CirceCodec {
  // do not remove, intellij lies ...
  import ViewportList.viewportListValidate

  implicit val codec: Codec[Fractal] = semiauto.deriveConfiguredCodec
}
