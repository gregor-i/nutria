package module.auth

import javax.inject.Singleton
import play.api.mvc.{Cookie, InjectedController}

@Singleton
class AuthenticationDummy() extends InjectedController with AuthenticationController {
  private val userData = UserInfo("dummy-id", "dummy@nutria-explorer.com", "/img/icon.png")
  private val userCookie = Cookie(name = "user", value = EncodeCookie(userData), httpOnly = false)

  def authenticate() = Action { _ =>
    Redirect("/")
      .withCookies(userCookie)
      .bakeCookies()
  }

  def logout() = Action { _ =>
    Redirect("/")
      .withCookies(userCookie.copy(value = ""))
      .bakeCookies()
  }
}
