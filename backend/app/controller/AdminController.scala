package controller

import java.util.UUID

import io.circe.JsonObject
import io.circe.syntax._
import javax.inject.Inject
import nutria.api.Entity
import nutria.core.Examples
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{ImageRepo, TemplateRepo, UserRepo}

class AdminController @Inject() (
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

  def deleteUser(id: String) = Action { req =>
    authenticator.adminUser(req) { _ =>
      userRepo.delete(id)
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
