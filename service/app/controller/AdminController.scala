package controller

import javax.inject.Inject
import module.SystemFractals
import nutria.core.FractalEntity
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{CachedFractalRepo, FractalRow}

class AdminController @Inject()(fractalRepo: CachedFractalRepo,
                                systemFractals: SystemFractals,
                                authenticator: Authenticator
                               ) extends InjectedController with Circe {

  def ui() = Action { req =>
    authenticator.adminUser(req) {
      val list = fractalRepo.list()
        .sortBy(_.maybeFractal.map(_.program))

      Ok(views.html.Admin(list))
    }
  }

  def deleteFractal(id: String) = Action { req =>
    authenticator.adminUser(req) {
      fractalRepo.delete(id)
      Ok
    }
  }

  def cleanFractals() = Action { req =>
    authenticator.adminUser(req) {
      fractalRepo.list()
        .collect {
          case FractalRow(id, None) => id
          case FractalRow(id, Some(fractalEntity)) if id != FractalEntity.id(fractalEntity) => id
        }
        .foreach(fractalRepo.delete)
      Ok
    }
  }

  def truncateFractals = Action { req =>
    authenticator.adminUser(req) {
      fractalRepo.list().map(_.id).foreach(fractalRepo.delete)
      Ok
    }
  }

  def insertSystemFractals = Action { req =>
    authenticator.adminUser(req) {
      systemFractals.systemFractals
        .foreach(entity => fractalRepo.save(
          FractalRow(
            FractalEntity.id(entity),
            Some(entity)
          )
        ))
      Ok
    }
  }
}
