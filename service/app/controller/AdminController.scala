package controller

import javax.inject.Inject
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalImageRepo, FractalRepo}

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
}
