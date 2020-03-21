package controller

import java.util.UUID

import io.circe.syntax._
import io.circe.JsonObject
import javax.inject.Inject
import model.FractalSorting
import module.SystemFractals
import nutria.core.User
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, FractalRow, UserRepo}

class AdminController @Inject() (
    fractalRepo: FractalRepo,
    userRepo: UserRepo,
    systemFractals: SystemFractals,
    authenticator: Authenticator
) extends InjectedController
    with Circe {

  def loadState() = Action { req =>
    authenticator.adminUser(req) { admin =>
      Ok(
        JsonObject(
          "admin" -> admin.asJson,
          "users" -> userRepo.list().asJson,
          "fractals" -> fractalRepo
            .list()
            .collect(fractalRepo.fractalRowToFractalEntity)
            .sorted(FractalSorting.orderingByProgram)
            .asJson
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
          case FractalRow(id, _, _, None) => id
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

  def insertSystemFractals = Action { req =>
    authenticator.adminUser(req) { admin =>
      systemFractals.systemFractals
        .foreach(
          entity =>
            fractalRepo.save(
              id = UUID.randomUUID().toString,
              owner = admin.id,
              fractal = entity
            )
        )
      Ok
    }
  }
}
