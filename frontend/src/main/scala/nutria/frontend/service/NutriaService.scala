package nutria.frontend.service

import nutria.api.{FractalEntity, FractalImageEntity, FractalTemplateEntity, FractalTemplateEntityWithId, User, Verdict, VoteStatistic, WithId}
import nutria.core._

import scala.concurrent.Future

// todo: order functions into objects
object NutriaService extends Service {
  def whoAmI(): Future[Option[User]] =
    get("/api/users/me")
      .flatMap(check(200))
      .flatMap(parse[Option[User]])

  // templates
  def loadAllTemplates(): Future[Seq[FractalTemplateEntityWithId]] =
    get(url = s"/api/templates")
      .flatMap(check(200))
      .flatMap(parse[Seq[FractalTemplateEntityWithId]])

  def loadTemplate(templateId: String): Future[FractalTemplateEntityWithId] =
    get(url = s"/api/templates/${templateId}")
      .flatMap(check(200))
      .flatMap(parse[FractalTemplateEntityWithId])

  def saveTemplate(template: FractalTemplateEntity): Future[FractalTemplateEntityWithId] =
    post(url = s"/api/templates", body = template)
      .flatMap(check(201))
      .flatMap(parse[FractalTemplateEntityWithId])

  def deleteTemplate(templateId: String): Future[Unit] =
    delete(s"/api/templates/${templateId}")
      .flatMap(check(204))
      .map(_ => ())

  def updateTemplate(template: FractalTemplateEntityWithId): Future[Unit] =
    put(url = s"/api/templates/${template.id}", body = template.entity)
      .flatMap(check(202))
      .map(_ => ())

  def loadUserTemplates(userId: String): Future[Vector[FractalTemplateEntityWithId]] =
    get(url = s"/api/users/${userId}/templates")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalTemplateEntityWithId]])

  // fractals
  def loadFractal(fractalId: String): Future[WithId[FractalImageEntity]] =
    get(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(parse[WithId[FractalImageEntity]])

  def loadPublicFractals(): Future[Vector[WithId[FractalImageEntity]]] =
    get(url = "/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[WithId[FractalImageEntity]]])

  def loadRandomFractal(): Future[FractalImage] =
    get(url = "/api/fractals/random")
      .flatMap(check(200))
      .flatMap(parse[FractalImage])

  def loadUserFractals(userId: String): Future[Vector[WithId[FractalImageEntity]]] =
    get(url = s"/api/users/${userId}/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[WithId[FractalImageEntity]]])

  def save(fractalEntity: FractalImageEntity): Future[WithId[FractalImageEntity]] =
    post("/api/fractals", fractalEntity)
      .flatMap(check(201))
      .flatMap(parse[WithId[FractalImageEntity]])

  def deleteFractal(fractalId: String): Future[Unit] =
    delete(s"/api/fractals/${fractalId}")
      .flatMap(check(204))
      .map(_ => ())

  def updateFractal(fractalEntity: WithId[FractalImageEntity]): Future[Unit] =
    put(s"/api/fractals/${fractalEntity.id}", fractalEntity.entity)
      .flatMap(check(202))
      .map(_ => ())

  // user
  def deleteUser(userId: String): Future[Unit] =
    delete(s"/api/users/${userId}")
      .flatMap(check(204))
      .map(_ => ())

  // votes
  def votes(): Future[Map[String, VoteStatistic]] =
    get("/api/votes")
      .flatMap(check(200))
      .flatMap(parse[Map[String, VoteStatistic]])

  def vote(fractalId: String, verdict: Verdict): Future[Unit] =
    put(s"/api/votes/${fractalId}", verdict)
      .flatMap(check(204))
      .map(_ => ())

  def deleteVote(fractalId: String): Future[Unit] =
    delete(s"/api/votes/${fractalId}")
      .flatMap(check(204))
      .map(_ => ())
}
