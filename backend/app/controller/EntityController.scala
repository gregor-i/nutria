package controller

import java.util.UUID

import io.circe.{Decoder, Encoder}
import nutria.api.{Entity, WithId}
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import play.mvc.Controller
import repo.EntityRepo
import io.circe.syntax._

import scala.util.chaining._

abstract class EntityController[E <: Entity[_]: Decoder: Encoder](entityRepo: EntityRepo[E], authenticator: Authenticator)(implicit
    ordering: Ordering[WithId[E]]
) extends InjectedController
    with Circe {
  def listPublic() = Action {
    entityRepo
      .listPublic()
      .collect(entityRepo.rowToEntity)
      .sorted(ordering)
      .asJson
      .pipe(Ok(_))
  }

  def listByUser(userId: String) = Action { req =>
    authenticator.byUserId(req)(userId) {
      entityRepo
        .listByUser(userId)
        .collect(entityRepo.rowToEntity)
        .sorted(ordering)
        .asJson
        .pipe(Ok(_))
    }
  }

  def get(id: String) = Action {
    entityRepo
      .get(id)
      .collect(entityRepo.rowToEntity) match {
      case Some(fractal) => Ok(fractal.asJson)
      case _             => NotFound
    }
  }

  def update(id: String) =
    Action(circe.tolerantJson[E]) { req =>
      entityRepo.get(id) match {
        case None => NotFound
        case Some(saved) =>
          authenticator.byUserId(req)(saved.owner) {
            entityRepo
              .save(
                id = id,
                owner = saved.owner,
                entity = req.body
              )
              .asJson
              .pipe(Ok(_))
          }
      }
    }

  def delete(id: String) = Action { req =>
    entityRepo.get(id) match {
      case None => NotFound
      case Some(saved) =>
        authenticator.byUserId(req)(saved.owner) {
          entityRepo.delete(id)
          NoContent
        }
    }
  }

  def post() = Action(circe.tolerantJson[E]) { request =>
    authenticator.withUser(request) { user =>
      entityRepo
        .save(
          id = UUID.randomUUID().toString,
          owner = user.id,
          entity = request.body
        )
        .asJson
        .pipe(Created(_))
    }
  }
}
