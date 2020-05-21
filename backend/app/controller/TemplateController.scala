package controller

import java.util.UUID

import javax.inject.Inject
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.TemplateRepo
import io.circe.syntax._
import nutria.api.{FractalTemplateEntity, WithId}

import scala.util.chaining._

class TemplateController @Inject() (templateRepo: TemplateRepo, authenticator: Authenticator) extends InjectedController with Circe {
  def listTemplates() = Action {
    templateRepo
      .list()
      .collect(templateRepo.rowToEntity)
      .asJson
      .pipe(Ok(_))
  }

  def getTemplate(templateId: String) = Action {
    templateRepo
      .get(templateId)
      .collect(templateRepo.rowToEntity) match {
      case Some(template) => Ok(template.asJson)
      case None           => NotFound
    }
  }

  def postTemplate() = Action(circe.tolerantJson[FractalTemplateEntity]) { req =>
    authenticator.withUser(req) { user =>
      val id = UUID.randomUUID().toString
      templateRepo.save(
        id = id,
        owner = user.id,
        entity = req.body
      )
      WithId(id, user.id, req.body).asJson
        .pipe(Created(_))
    }
  }

  def updateTemplate(templateId: String) =
    Action(circe.tolerantJson[FractalTemplateEntity]) { req =>
      templateRepo.get(templateId) match {
        case None => NotFound
        case Some(savedTemplate) =>
          authenticator.byUserId(req)(savedTemplate.owner) {
            templateRepo.save(
              id = templateId,
              owner = savedTemplate.owner,
              entity = req.body
            )
            Accepted
          }
      }
    }

}
