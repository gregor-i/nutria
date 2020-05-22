package controller

import java.util.UUID

import io.circe.JsonObject
import io.circe.syntax._
import javax.inject.Inject
import model.FractalSorting
import nutria.api.{Entity, WithId}
import nutria.core.{Examples, Fractal, ViewportList}
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, TemplateRepo, UserRepo}

class AdminController @Inject() (
    fractalRepo: FractalRepo,
    templateRepo: TemplateRepo,
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
          "fractals"  -> fractalRepo.list().asJson
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
}
