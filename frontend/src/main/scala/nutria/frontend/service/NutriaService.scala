package nutria.frontend.service

import nutria.api._
import nutria.frontend.service.Service._

import scala.concurrent.Future

object NutriaService {

  // votes
  @deprecated
  def votes(): Future[Map[String, VoteStatistic]] =
    get("/api/votes")
      .flatMap(check(200))
      .flatMap(parse[Map[String, VoteStatistic]])

  @deprecated
  def vote(fractalId: String, verdict: Verdict): Future[Unit] =
    put(s"/api/votes/${fractalId}", verdict)
      .flatMap(check(204))
      .map(_ => ())

  @deprecated
  def deleteVote(fractalId: String): Future[Unit] =
    delete(s"/api/votes/${fractalId}")
      .flatMap(check(204))
      .map(_ => ())
}
