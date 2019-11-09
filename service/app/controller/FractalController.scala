package controller

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, FractalRow}

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

  def deleteFractal(id: String) = Action {
    fractalRepo.delete(id)
    Ok
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    // todo: check / correct aspect ratio
    val id = FractalEntity.id(request.body)
    fractalRepo.save(FractalRow(
      id = id,
      owner = ???,
      published = ???,
      maybeFractal = Some(request.body)
    ))
    Created(FractalEntityWithId(id, ???, ???, request.body).asJson)
  }
}
