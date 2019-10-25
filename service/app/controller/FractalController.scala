package controller

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{CachedFractalRepo, FractalRow}

class FractalController @Inject()(fractalRepo: CachedFractalRepo) extends InjectedController with Circe {

  def listFractals() = Action {
    Ok {
      fractalRepo.list()
        .collect { case FractalRow(id, Some(entity)) => FractalEntityWithId(id, entity) }
        .sorted
        .asJson
    }
  }

  def getFractal(id: String) = Action {
    fractalRepo.get(id).flatMap(_.maybeFractal) match {
      case Some(fractal) => Ok(fractal.asJson)
      case _ => NotFound
    }
  }

  def deleteFractal(id: String) = Action {
    fractalRepo.delete(id)
    Ok
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    // todo: check / correct aspect ratio
    val id = FractalEntity.id(request.body)
    fractalRepo.save(FractalRow(
      id = id,
      maybeFractal = Some(request.body)
    ))
    Created(FractalEntityWithId(id, request.body).asJson)
  }
}
