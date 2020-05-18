package controller

import java.util.UUID

import javax.inject.Inject
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.TemplateRepository
import io.circe.syntax._
import nutria.api.{FractalTemplateEntity, WithId}

import scala.util.chaining._

class TemplateController @Inject() (templateRepo: TemplateRepository, authenticator: Authenticator) extends InjectedController with Circe {
  def listTemplates() = Action {
    templateRepo
      .list()
      .collect(templateRepo.fractalRowToTemplateEntity)
      .asJson
      .pipe(Ok(_))
  }

  def getTemplate(templateId: String) = Action {
    templateRepo
      .get(templateId)
      .collect(templateRepo.fractalRowToTemplateEntity) match {
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
        template = req.body
      )
      WithId(id, user.id, req.body).asJson
        .pipe(Created(_))
    }
  }

}
