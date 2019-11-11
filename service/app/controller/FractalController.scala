package controller

import java.util.UUID

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, FractalRow}

import scala.util.chaining._
class FractalController @Inject()(fractalRepo: FractalRepo,
                                  authenticator: Authenticator) extends InjectedController with Circe {

  def listPublicFractals() = Action {
    Ok {
      fractalRepo.listPublic()
        .collect(fractalRowToFractalEntity)
        .sorted
        .asJson
    }
  }

  def listUserFractals(userId: String) = Action { req =>
    authenticator.byUserId(req)(userId) {
      Ok {
        fractalRepo.listByUser(userId)
          .collect(fractalRowToFractalEntity)
          .sorted
          .asJson
      }
    }
  }

  val fractalRowToFractalEntity: PartialFunction[FractalRow, FractalEntityWithId] = {
    case FractalRow(id, owner, published, Some(entity)) => FractalEntityWithId(id, owner, published, entity)
  }

  def getFractal(id: String) = Action {
    fractalRepo.get(id).flatMap(_.maybeFractal) match {
      case Some(fractal) => Ok(fractal.asJson)
      case _ => NotFound
    }
  }

  def updateUserFractal(userId: String, fractalId: String) =
    Action(circe.tolerantJson[FractalEntityWithId]) { req =>
      authenticator.byUserId(req)(userId) {
        fractalRepo.save(FractalRow(
          id = fractalId,
          owner = userId,
          published = false,
          maybeFractal = Some(req.body.entity)
        ))
        Accepted
      }
    }

  def deleteUserFractal(userId: String, fractalId: String) = Action {req =>
    authenticator.byUserId(req)(userId){
      fractalRepo.delete(userId, fractalId)
      Ok
    }
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    authenticator.withUser(request) { user =>
      // todo: check / correct aspect ratio
      val id = UUID.randomUUID().toString
      fractalRepo.save(FractalRow(
        id = id,
        owner = user.id,
        published = false,
        maybeFractal = Some(request.body)
      ))
      FractalEntityWithId(id, user.id, published = false, request.body)
        .asJson
        .pipe(Created(_))
    }
  }
}
