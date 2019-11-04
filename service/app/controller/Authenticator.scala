package controller

import javax.inject.Inject
import module.auth.DecodeCookie
import nutria.core.User
import play.api.Configuration
import play.api.mvc.{ControllerComponents, InjectedController, Request, Result, Results}


class Authenticator @Inject()(conf: Configuration) extends Results{

  private val adminEmail = conf.get[String]("auth.admin.email")

  def adminUser[A](req: Request[A])(ifAuthorized: => Result): Result = {
    val user = req.cookies.get("user")
      .map(_.value)
      .flatMap(DecodeCookie.apply[User])

    user match {
      case None => Unauthorized
      case Some(otherUser) if otherUser.email != adminEmail => Forbidden
      case Some(_) => ifAuthorized
    }
  }

}
