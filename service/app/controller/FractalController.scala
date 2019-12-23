package controller

import java.util.UUID

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import nutria.core.viewport.DefaultViewport
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, FractalRow}

import scala.util.Random
import scala.util.chaining._

class FractalController @Inject()(fractalRepo: FractalRepo,
                                  authenticator: Authenticator) extends InjectedController with Circe {

  def listPublicFractals() = Action {
    Ok {
      fractalRepo.listPublic()
        .collect(fractalRepo.fractalRowToFractalEntity)
        .sorted
        .asJson
    }
  }

  def listUserFractals(userId: String) = Action { req =>
    authenticator.byUserId(req)(userId) {
      Ok {
        fractalRepo.listByUser(userId)
          .collect(fractalRepo.fractalRowToFractalEntity)
          .sorted
          .asJson
      }
    }
  }


  def getFractal(id: String) = Action {
    (for {
      fractalRow <- fractalRepo.get(id)
      fractalEntity = fractalRepo.fractalRowToFractalEntity.lift.apply(fractalRow)
    } yield fractalEntity) match {
      case Some(fractal) => Ok(fractal.asJson)
      case _ => NotFound
    }
  }

  def getRandomFractal() = Action {
    val seed = java.time.Instant.now.truncatedTo(java.time.temporal.ChronoUnit.DAYS).toEpochMilli
    val random = new Random(seed = seed)
    val entities = fractalRepo.listPublic()
      .collect(fractalRepo.fractalRowToFractalEntity)
    if (entities.isEmpty) {
      val defaultImage = FractalImage(program = NewtonIteration.default, view = DefaultViewport.defaultViewport)
      Ok(defaultImage.asJson)
    } else {
      val images = FractalImage.allImages(entities.map(_.entity))
      val randomImage = images(random.nextInt(images.length))
      Ok(randomImage.asJson)
    }
  }

  def updateUserFractal(userId: String, fractalId: String) =
    Action(circe.tolerantJson[FractalEntity]) { req =>
      authenticator.byUserId(req)(userId) {
        fractalRepo.save(
          id = fractalId,
          owner = userId,
          fractal = req.body
        )
        Accepted
      }
    }

  def deleteUserFractal(userId: String, fractalId: String) = Action { req =>
    authenticator.byUserId(req)(userId) {
      fractalRepo.delete(userId, fractalId)
      Ok
    }
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    authenticator.withUser(request) { user =>
      // todo: check / correct aspect ratio
      val id = UUID.randomUUID().toString
      fractalRepo.save(
        id = id,
        owner = user.id,
        fractal = request.body
      )
      FractalEntityWithId(id, user.id, request.body)
        .asJson
        .pipe(Created(_))
    }
  }
}
