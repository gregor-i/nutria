package controller

import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import play.api.libs.circe.Circe
import play.api.mvc.{DiscardingCookie, InjectedController}
import repo.UserRepo

import scala.util.chaining._

@Singleton
class UserController @Inject() (userRepo: UserRepo, authenticator: Authenticator)
    extends InjectedController
    with Circe {
  def get(userId: String) = Action { req =>
    authenticator.adminUser(req) {
      userRepo.get(userId) match {
        case Some(user) => Ok(user.asJson)
        case None       => NotFound
      }
    }
  }

  def delete(userId: String) = Action { req =>
    authenticator.byUserId(req)(userId) {
      println(userRepo.get(userId))

      (userRepo.delete(userId) match {
        case 0 => NotFound
        case _ => NoContent
      }).discardingCookies(DiscardingCookie("user"))
        .bakeCookies()
    }
  }

  def me() = Action { req =>
    authenticator
      .userFromCookie(req)
      .asJson
      .pipe(Ok(_))
  }
}
