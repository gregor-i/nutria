package module.auth

import java.util.UUID

import javax.inject.Singleton
import nutria.core.User
import play.api.mvc.{Cookie, InjectedController}
import repo.UserRepo

@Singleton
class AuthenticationDummy(repo: UserRepo) extends InjectedController with AuthenticationController {
  def authenticate() = Action { implicit req =>
    repo.save(AuthenticationDummy.user)
    Redirect("/")
      .addingToSession("uesr-id" -> AuthenticationDummy.user.id)
  }

  def logout() = Action { _ =>
    Redirect("/").withNewSession
  }
}

object AuthenticationDummy {
  val user = User(
    UUID.nameUUIDFromBytes("dummy-id".getBytes).toString,
    "Dummy Name",
    "dummy@nutria-explorer.com",
    googleUserId = None
  )
}
