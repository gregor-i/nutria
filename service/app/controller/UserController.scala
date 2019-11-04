package controller

import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import play.api.libs.circe.Circe
import play.api.mvc.InjectedController
import repo.UserRepo

@Singleton
class UserController @Inject()(userRepo: UserRepo, authenticator: Authenticator) extends InjectedController with Circe {
  def get(userId: String) = Action { req =>
    authenticator.adminUser(req) {
      userRepo.get(userId) match {
        case Some(user) => Ok(user.user.asJson)
        case None => NotFound
      }
    }
  }
}
