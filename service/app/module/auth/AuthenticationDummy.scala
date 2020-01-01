package module.auth

import java.util.UUID

import javax.inject.{Inject, Singleton}
import nutria.core.User
import play.api.mvc.InjectedController
import repo.UserRepo

@Singleton
class AuthenticationDummy @Inject() (repo: UserRepo) extends InjectedController with AuthenticationController {
  val user = User(
    UUID.nameUUIDFromBytes("dummy-id".getBytes).toString,
    "Dummy Name",
    "dummy@nutria-explorer.com",
    googleUserId = None
  )

  def authenticate() = Action { implicit req =>
    repo.save(user)
    Redirect(req.getQueryString("return-to").getOrElse("/"))
      .addingToSession("user-id" -> user.id)
  }

  def logout() = Action { _ =>
    Redirect("/").withNewSession
  }
}
