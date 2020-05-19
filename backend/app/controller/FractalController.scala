package controller

import java.util.UUID

import io.circe.syntax._
import javax.inject.Inject
import model.FractalSorting
import nutria.api.WithId
import nutria.core.{Viewport, _}
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, VotesRepo}

import scala.util.Random
import scala.util.chaining._

class FractalController @Inject() (fractalRepo: FractalRepo, votesRepo: VotesRepo, authenticator: Authenticator)
    extends InjectedController
    with Circe {

  def listPublicFractals() = Action {
    fractalRepo
      .listPublic()
      .collect(fractalRepo.rowToEntity)
      .sorted(FractalSorting.ordering(votesRepo.getAll()))
      .asJson
      .pipe(Ok(_))
  }

  def listUserFractals(userId: String) = Action { req =>
    authenticator.byUserId(req)(userId) {
      fractalRepo
        .listByUser(userId)
        .collect(fractalRepo.rowToEntity)
        .sorted(FractalSorting.orderingByProgram)
        .asJson
        .pipe(Ok(_))
    }
  }

  def getFractal(id: String) = Action {
    (for {
      fractalRow <- fractalRepo.get(id)
      fractalEntity = fractalRepo.rowToEntity.lift.apply(fractalRow)
    } yield fractalEntity) match {
      case Some(fractal) => Ok(fractal.asJson)
      case _             => NotFound
    }
  }

  def getRandomFractal() = Action {
    val seed   = java.time.Instant.now.truncatedTo(java.time.temporal.ChronoUnit.DAYS).toEpochMilli
    val random = new Random(seed = seed)
    val entities = fractalRepo
      .listPublic()
      .collect(fractalRepo.rowToEntity)
    if (entities.isEmpty) {
      val defaultImage =
        FractalImage(template = Examples.newtonIteration, viewport = Viewport.aroundZero)
      Ok(defaultImage.asJson)
    } else {
      val images      = FractalImage.allImages(entities.map(_.entity))
      val randomImage = images(random.nextInt(images.length))
      Ok(randomImage.asJson)
    }
  }

  def updateFractal(fractalId: String) =
    Action(circe.tolerantJson[FractalEntity]) { req =>
      fractalRepo.get(fractalId) match {
        case None => NotFound
        case Some(savedFractal) =>
          authenticator.byUserId(req)(savedFractal.owner) {
            fractalRepo.save(
              id = fractalId,
              owner = savedFractal.owner,
              entity = req.body
            )
            Accepted
          }
      }
    }

  def deleteFractal(fractalId: String) = Action { req =>
    fractalRepo.get(fractalId) match {
      case None => NotFound
      case Some(savedFractal) =>
        authenticator.byUserId(req)(savedFractal.owner) {
          fractalRepo.delete(fractalId)
          Ok
        }
    }
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    authenticator.withUser(request) { user =>
      // todo: check / correct aspect ratio
      val id = UUID.randomUUID().toString
      fractalRepo.save(
        id = id,
        owner = user.id,
        entity = request.body
      )
      WithId(id, user.id, request.body).asJson
        .pipe(Created(_))
    }
  }
}
