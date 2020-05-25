package controller

import java.util.UUID

import io.circe.JsonObject
import io.circe.syntax._
import javax.inject.Inject
import model.FractalSorting
import nutria.api.{Entity, WithId}
import nutria.core.{Examples, Fractal, FractalImage, ViewportList}
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, ImageRepo, TemplateRepo, UserRepo}

import scala.util.chaining._

class AdminController @Inject() (
    fractalRepo: FractalRepo,
    templateRepo: TemplateRepo,
    imageRepo: ImageRepo,
    userRepo: UserRepo,
    authenticator: Authenticator
) extends InjectedController
    with Circe {

  def loadState() = Action { req =>
    authenticator.adminUser(req) { admin =>
      Ok(
        JsonObject(
          "admin"     -> admin.asJson,
          "users"     -> userRepo.list().asJson,
          "templates" -> templateRepo.list().asJson,
          "fractals"  -> imageRepo.list().asJson
        ).asJson
      )
    }
  }

  def deleteFractal(id: String) = Action { req =>
    authenticator.adminUser(req) { _ =>
      fractalRepo.delete(id)
      Ok
    }
  }

  def deleteUser(id: String) = Action { req =>
    authenticator.adminUser(req) { _ =>
      userRepo.delete(id)
      Ok
    }
  }

  def cleanFractals() = Action { req =>
    authenticator.adminUser(req) { _ =>
      fractalRepo
        .list()
        .collect {
          case WithId(id, _, None) => id
        }
        .foreach(fractalRepo.delete)
      Ok
    }
  }

  def truncateFractals = Action { req =>
    authenticator.adminUser(req) { _ =>
      fractalRepo.list().map(_.id).foreach(fractalRepo.delete)
      Ok
    }
  }

  def insertExamples = Action { req =>
    authenticator.adminUser(req) { admin =>
      Examples.allNamed
        .foreach {
          case (name, template) =>
            templateRepo.save(
              id = UUID.randomUUID().toString,
              owner = admin.id,
              entity = Entity(
                title = name,
                value = template
              )
            )
        }
      Ok
    }
  }

  def migrateFractals = Action { req =>
    authenticator.adminUser(req) { admin =>
      fractalRepo
        .list()
        .collect(fractalRepo.rowToEntity)
        .foreach(fractalRepo.save)
      Ok
    }
  }

  def toImages = Action {
    val templates    = templateRepo.list()
    val templatesMap = templates.groupBy(_.entity.get.value.code).mapValues(_.head)

    val fractals = fractalRepo
      .list()
      .groupBy(_.entity.get.value.program.code)
      .map {
        case (code, values) =>
          (code, templatesMap.get(code), values)
      }
      .toSeq

    fractalRepo
      .list()
      .foreach { withId =>
        val entity = withId.entity.get
        val value  = entity.value
        val images = FractalImage.allImages(Seq(value))

        images.foreach { image =>
          imageRepo.save(
            id = UUID.randomUUID().toString,
            owner = withId.owner,
            entity = Entity(
              title = entity.title,
              description = entity.description,
              published = entity.published,
              value = image
            )
          )
        }

        fractalRepo.delete(withId.id)
      }

    Ok
  }
}
