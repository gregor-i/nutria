package module.auth

import java.util.UUID

import javax.inject.Singleton
import nutria.core.User
import play.api.mvc.{Cookie, InjectedController}

@Singleton
class AuthenticationDummy() extends InjectedController with AuthenticationController {
  private def userCookie = Cookie(name = "user", value = EncodeCookie(AuthenticationDummy.user))

  def authenticate() = Action { _ =>
    Redirect("/")
      .withCookies(userCookie)
      .bakeCookies()
  }

  def logout() = Action { _ =>
    Redirect("/")
      .withCookies(Cookie(name = "user", value = ""))
      .bakeCookies()
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
