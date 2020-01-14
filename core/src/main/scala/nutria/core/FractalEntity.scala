package nutria.core

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.numeric.NonNegative
import io.circe.syntax._
import io.circe.{Codec, Decoder, Encoder}
import monocle.Lens
import nutria.core.viewport.DefaultViewport
@monocle.macros.Lenses()
case class FractalEntity(
    title: String = "",
    program: FractalProgram,
    views: List[Viewport] Refined NonEmpty = refineV[NonEmpty](List(DefaultViewport.defaultViewport)).toOption.get,
    description: String = "",
    reference: List[String] = List.empty,
    antiAliase: Int Refined Positive = refineMV(1),
    published: Boolean = false,
    upvotes: Int Refined NonNegative = refineMV(0),
    downvotes: Int Refined NonNegative = refineMV(0)
) {
  def acceptance = {
    val votes = upvotes.value + downvotes.value
    if (votes == 0)
      0.5
    else
      upvotes.value.toDouble / votes.toDouble
  }
}

object FractalEntity extends CirceCodex {
  import Ordering.Double.TotalOrdering
  implicit val ordering: Ordering[FractalEntity] = Ordering.by { entity =>
    (entity.acceptance, entity.program)
  }

  implicit val codec: Codec[FractalEntity] = semiauto.deriveConfiguredCodec
}

@monocle.macros.Lenses()
case class FractalEntityWithId(id: String, owner: String, entity: FractalEntity)

object FractalEntityWithId extends CirceCodex {
  val viewports: Lens[FractalEntityWithId, Refined[List[Viewport], NonEmpty]] =
    FractalEntityWithId.entity.composeLens(FractalEntity.views)

  implicit val codec: Codec[FractalEntityWithId] = Codec.from(
    decodeA = Decoder[FractalEntityWithId] { json =>
      for {
        entity <- json.as[FractalEntity]
        id     <- json.downField("id").as[String]
        owner  <- json.downField("owner").as[String]
      } yield FractalEntityWithId(id, owner, entity)
    },
    encodeA = Encoder[FractalEntityWithId] { row =>
      Encoder[FractalEntity]
        .apply(row.entity)
        .mapObject(_.add("id", row.id.asJson))
        .mapObject(_.add("owner", row.owner.asJson))
    }
  )
}
