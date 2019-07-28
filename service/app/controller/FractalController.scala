package controller

import java.util.UUID

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalImageRepo, FractalRepo, FractalRow}

class FractalController @Inject()(fractalRepo: FractalRepo,
                                  fractalImageRepo: FractalImageRepo
                                 ) extends InjectedController with Circe {
  private def fractals: List[FractalEntityWithId] =
    fractalRepo.list()
      .collect { case FractalRow(id, Some(entity)) => FractalEntityWithId(id, entity) }
      .sortBy(_.id)

  def listFractals() = Action {
    Ok(fractals.asJson)
  }

  def getFractal(id: String) = Action {
    fractalRepo.get(id).flatMap(_.maybeFractal) match {
      case Some(fractal) => Ok(fractal.asJson)
      case _ => NotFound
    }
  }

  def postFractal() = Action(circe.tolerantJson[FractalEntity]) { request =>
    fractalRepo.save(FractalRow(
      id = UUID.randomUUID().toString,
      maybeFractal = Some(request.body)
    ))
    Ok(fractals.asJson)
  }

  def image(id: String) = Action { request =>
    (for {
      fractal <- fractalRepo.get(id).flatMap(_.maybeFractal)
        .toRight(NotFound(views.xml.RenderingError("not found")).as("image/svg+xml"))
      etag = fractal.hashCode().toString

      _ <- request.headers.get("If-None-Match") match {
        case Some(cachedEtag) if cachedEtag == etag => Left(NotModified)
        case _ => Right(())
      }

      _ <- fractalImageRepo.get(id).toLeft(())
        .left.map { bytes =>
        Ok(bytes)
          .as("image/png")
          .withHeaders("ETag" -> etag)
      }
    } yield {
      PartialContent(views.xml.RenderingError("processing"))
        .as("image/svg+xml")
    }).merge
  }
}
