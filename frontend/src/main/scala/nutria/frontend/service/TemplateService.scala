package nutria.frontend.service

import nutria.api.{FractalTemplateEntity, FractalTemplateEntityWithId}
import nutria.frontend.service.Service._

import scala.concurrent.Future

object TemplateService {
  def get(templateId: String): Future[FractalTemplateEntityWithId] =
    Service
      .get(url = s"/api/templates/${templateId}")
      .flatMap(check(200))
      .flatMap(parse[FractalTemplateEntityWithId])

  def post(template: FractalTemplateEntity): Future[FractalTemplateEntityWithId] =
    Service
      .post(url = s"/api/templates", body = template)
      .flatMap(check(201))
      .flatMap(parse[FractalTemplateEntityWithId])

  def delete(templateId: String): Future[Unit] =
    Service
      .delete(s"/api/templates/${templateId}")
      .flatMap(check(204))
      .map(_ => ())

  def put(template: FractalTemplateEntityWithId): Future[FractalTemplateEntityWithId] =
    Service
      .put(url = s"/api/templates/${template.id}", body = template.entity)
      .flatMap(check(200))
      .flatMap(parse[FractalTemplateEntityWithId])

  def listPublic(): Future[Seq[FractalTemplateEntityWithId]] =
    Service
      .get(url = s"/api/templates")
      .flatMap(check(200))
      .flatMap(parse[Seq[FractalTemplateEntityWithId]])

  def listUser(userId: String): Future[Vector[FractalTemplateEntityWithId]] =
    Service
      .get(url = s"/api/users/${userId}/templates")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalTemplateEntityWithId]])
}
