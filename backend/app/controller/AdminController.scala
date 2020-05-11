package controller

import io.circe.syntax._
import io.circe.JsonObject
import javax.inject.Inject
import model.FractalSorting
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, FractalRow, UserRepo}

class AdminController @Inject() (
    fractalRepo: FractalRepo,
    userRepo: UserRepo,
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

  def migrateAllFractals = Action { req =>
    authenticator.adminUser(req) { _ =>
      fractalRepo
        .list()
        .foreach {
          case FractalRow(id, owner, _, Some(fractal)) => fractalRepo.save(id, owner, fractal)
          case _                                       => ()
        }
      Ok
    }
  }
}
