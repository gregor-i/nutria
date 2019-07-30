package controller

import javax.inject.Inject
import nutria.core.FractalEntity
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalImageRepo, FractalRepo, FractalRow}

class AdminController @Inject()(fractalRepo: FractalRepo,
                                fractalImageRepo: FractalImageRepo
                                 ) extends InjectedController with Circe {

  def deleteFractal(id : String) = Action{
    fractalRepo.delete(id)
    Ok
  }

  def truncateImages() = Action{
    fractalImageRepo.truncate()
    Ok
  }

  def cleanFractals() = Action{
    fractalRepo.list()
      .collect{
        case FractalRow(id, None) => id
        case FractalRow(id, Some(fractalEntity)) if id != FractalEntity.id(fractalEntity) => id
      }
      .foreach(fractalRepo.delete)
    FractalEntity.systemFractals
        .foreach(entity => fractalRepo.save(
          FractalRow(
            FractalEntity.id(entity),
            Some(entity)
          )
        ))
    Ok
  }
}
