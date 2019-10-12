package controller

import java.io.ByteArrayInputStream
import java.util.Base64

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import play.api.http.HeaderNames
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.{FractalImageRepo, FractalRepo, FractalRow, Hasher}

import scala.util.Try

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
    // todo: check / correct aspect ratio
    val id = FractalEntity.id(request.body)
    fractalRepo.save(FractalRow(
      id = id,
      maybeFractal = Some(request.body)
    ))
    Created(FractalEntityWithId(id, request.body).asJson)
  }

  def getImage(id: String) = Action { request =>
    (for {
      fractal <- fractalRepo.get(id).flatMap(_.maybeFractal).toRight {
        NotFound(views.xml.RenderingError("not found"))
          .as("image/svg+xml")
      }

      etag <- fractalImageRepo.getHash(id).toRight {
        PartialContent(views.xml.RenderingError("not rendered"))
          .as("image/svg+xml")
      }

      _ <- request.headers.get(HeaderNames.IF_NONE_MATCH) match {
        case Some(cachedEtag) if cachedEtag == etag => Left(NotModified)
        case _ => Right(())
      }

      bytes <- fractalImageRepo.getImage(id).toRight {
        PartialContent(views.xml.RenderingError("not rendered"))
          .as("image/svg+xml")
      }

    } yield {
      Ok(bytes)
        .as("image/png")
        .withHeaders(HeaderNames.ETAG -> etag)
    }).merge
  }

  def putImage(id: String) = Action { request =>
    (for {
      _ <- request.mediaType
        .filter(_.mediaType == "image")
        .filter(_.mediaSubType == "png")
        .toRight(BadRequest("incorrect content type"))

      rawBytes <- request.body.asRaw
        .flatMap(_.asBytes(1000*1000*1000))  // 1MB
        .map(_.toArray).toRight(BadRequest("request body is not parseable"))

      decodedBytes = Base64.getDecoder.decode(rawBytes)

      bufferdImage <- Try{
        javax.imageio.ImageIO.read(new ByteArrayInputStream(decodedBytes))
      }
        .toOption
        .filter(_ != null)
        .filter(_.getWidth == 400)
        .filter(_.getHeight == 225)
        .toRight(BadRequest("request body is no valid image"))

      hash = Hasher(decodedBytes)
      _ = fractalImageRepo.save(id, hash, decodedBytes)
    } yield Ok).merge
  }
}
