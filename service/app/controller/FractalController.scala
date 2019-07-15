package controller

import java.util.UUID

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalRepo, FractalRow}

class FractalController @Inject()(repo: FractalRepo) extends InjectedController with Circe {
  def listFractals() = Action {
    val fractals = FractalEntity.systemFractals ++ repo.list().flatMap(_.maybeFractal)
    Ok(fractals.asJson)
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    repo.save(FractalRow(
      id = UUID.randomUUID().toString,
      maybeFractal = Some( request.body)
    ))
    val fractals = FractalEntity.systemFractals ++ repo.list().flatMap(_.maybeFractal)
    Ok(fractals.asJson)
  }
}
