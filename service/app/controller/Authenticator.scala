package controller

import javax.inject.Inject
import module.auth.DecodeCookie
import nutria.core.User
import play.api.Configuration
import play.api.mvc.{Request, Result, Results}


class Authenticator @Inject()(conf: Configuration) extends Results {

  private val adminEmail = conf.get[String]("auth.admin.email")

  def adminUser[A](req: Request[A])(ifAuthorized: => Result): Result = {
    userFromCookie(req) match {
      case None => Unauthorized
      case Some(otherUser) if otherUser.email != adminEmail => Forbidden
      case Some(_) => ifAuthorized
    }
  }

  def byUserId[A](req: Request[A])(userId: String)(ifAuthorized: => Result): Result = {
    userFromCookie(req) match {
      case None => Unauthorized
      case Some(otherUser) if otherUser.id != userId => Forbidden
      case Some(_) => ifAuthorized
    }
  }

  def withUser[A](req: Request[A])(ifAuthorized: User => Result): Result = {
    userFromCookie(req) match {
      case None => Unauthorized
      case Some(user) => ifAuthorized(user)
    }
  }

  // todo: take from session instead. the user cookie might be tempered with ...
  def userFromCookie[A](req: Request[A]): Option[User] =
    req.cookies.get("user")
      .map(_.value)
      .flatMap(DecodeCookie.apply[User])

}
