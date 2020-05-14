package nutria.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{NonNegative, Positive}
import io.circe.Codec
import nutria.CirceCodec

@monocle.macros.Lenses()
case class FractalEntity(
    title: String = "",
    description: String = "",
    reference: List[String] = List.empty,
    published: Boolean = false,
    program: FreestyleProgram,
    views: ViewportList = ViewportList.refineUnsafe(List(Viewport.aroundZero)),
    antiAliase: Int Refined Positive = refineUnsafe(1),
    upvotes: Int Refined NonNegative = refineUnsafe(0),
    downvotes: Int Refined NonNegative = refineUnsafe(0)
)

object FractalEntity extends CirceCodec {
  // do not remove, intellij lies ...
  import ViewportList.viewportListValidate

  implicit val codec: Codec[FractalEntity] = semiauto.deriveConfiguredCodec
}
