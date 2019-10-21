package controller

import javax.inject.Inject
import module.SystemFractals
import nutria.core.FractalEntity
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalImageRepo, FractalRepo, FractalRow}

class AdminController @Inject()(fractalRepo: FractalRepo,
                                fractalImageRepo: FractalImageRepo,
                                systemFractals: SystemFractals
                                 ) extends InjectedController with Circe {

  def ui() = Action{
    val list = fractalRepo.list()
      .sortBy(_.maybeFractal.map(_.program))

    Ok(views.html.Admin(list))
  }

  def deleteFractal(id : String) = Action{
    fractalRepo.delete(id)
    Ok
  }

  def cleanFractals() = Action{
    fractalRepo.list()
      .collect{
        case FractalRow(id, None) => id
        case FractalRow(id, Some(fractalEntity)) if id != FractalEntity.id(fractalEntity) => id
      }
      .foreach(fractalRepo.delete)
    Ok
  }

  def truncateFractals = Action {
    fractalRepo.list().map(_.id).foreach(fractalRepo.delete)
    Ok
  }

  def insertSystemFractals = Action {
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
