package controller

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.libs.circe.Circe
import play.api.mvc.{Headers, InjectedController}
import repo.{FractalImageRepo, FractalRepo, FractalRow}

class FractalController @Inject()(fractalRepo: FractalRepo,
                                  fractalImageRepo: FractalImageRepo
                                 ) extends InjectedController with Circe {

  def listFractals() = Action {
    Ok {
      fractalRepo.list()
        .collect { case FractalRow(id, Some(entity)) => FractalEntityWithId(id, entity) }
        .sortBy(_.entity.program)
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
    fractalRepo.save(FractalRow(
      id = FractalEntity.id(request.body),
      maybeFractal = Some(request.body)
    ))
    Ok
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

      _ <- fractal.program match {
        case _: FreestyleProgram => Left(NotImplemented(views.xml.RenderingError("not implemented")).as("image/svg+xml"))
        case _ => Right(())
      }

      _ <- fractalImageRepo.get(id).toLeft(())
        .left.map { bytes =>
        Ok(bytes)
          .as("image/png")
          .withHeaders("ETag" -> etag)
          .withHeaders("Cache-Control" -> "public, max-age=31536000") // 1 year
      }
    } yield {
      PartialContent(views.xml.RenderingError("processing"))
        .as("image/svg+xml")
    }).merge
  }
}
