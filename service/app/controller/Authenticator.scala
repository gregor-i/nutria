package controller

import javax.inject.Inject
import nutria.core.User
import play.api.Configuration
import play.api.mvc.{Request, Result, Results}
import repo.UserRepo

class Authenticator @Inject() (conf: Configuration, userRepo: UserRepo) extends Results {

  private val adminEmail = conf.get[String]("auth.admin.email")

  def adminUser[A](req: Request[A])(ifAuthorized: => Result): Result =
    userFromSessionAndDb(req) match {
      case None                                             => Unauthorized
      case Some(otherUser) if otherUser.email != adminEmail => Forbidden
      case Some(_)                                          => ifAuthorized
    }

  def byUserId[A](req: Request[A])(userId: String)(ifAuthorized: => Result): Result =
    userFromSessionAndDb(req) match {
      case None                                      => Unauthorized
      case Some(otherUser) if otherUser.id != userId => Forbidden
      case Some(_)                                   => ifAuthorized
    }

  def withUser[A](req: Request[A])(ifAuthorized: User => Result): Result =
    userFromSessionAndDb(req) match {
      case None       => Unauthorized
      case Some(user) => ifAuthorized(user)
    }

  private def userFromSessionAndDb(req: Request[_]): Option[User] =
    for {
      userId <- req.session.get("user-id")
      user   <- userRepo.get(userId)
    } yield user

  def getUser(req: Request[_]): Option[User] = userFromSessionAndDb(req)
}
