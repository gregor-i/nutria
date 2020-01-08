package controller

import javax.inject.{Inject, Singleton}
import nutria.core.{DownVote, UpVote, Verdict, Vote, VoteStatistic}
import play.api.mvc.InjectedController
import io.circe.syntax._
import play.api.libs.circe.Circe
import repo.VotesRepository

import scala.util.chaining._

@Singleton()
class VotesController @Inject() (votesRepo: VotesRepository, authenticator: Authenticator) extends InjectedController with Circe {
  def getAll() = Action { req =>
    val maybeUser = authenticator.getUser(req)
    votesRepo
      .getAll()
      .groupBy(_.forFractal)
      .view
      .mapValues(
        votes =>
          VoteStatistic(
            upvotes = votes.count(_.verdict == UpVote),
            downvotes = votes.count(_.verdict == DownVote),
            yourVerdict = maybeUser.flatMap(user => votes.find(_.byUser == user.id)).map(_.verdict)
          )
      )
      .toMap
      .pipe(_.asJson)
      .pipe(Ok(_))
  }

  def vote(fractalId: String) = Action(circe.tolerantJson[Verdict]) { req =>
    authenticator.withUser(req) { user =>
      val vote = Vote(forFractal = fractalId, byUser = user.id, verdict = req.body)
      votesRepo.upsert(vote)
      NoContent
    }
  }

  def deleteVote(fractalId: String) = Action { req =>
    authenticator.withUser(req) { user =>
      votesRepo.delete(fractalId, user.id)
      NoContent
    }
  }
}
