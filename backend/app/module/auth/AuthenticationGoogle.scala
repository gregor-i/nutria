package module.auth

import com.google.inject.Inject
import io.lemonlabs.uri.Url
import javax.inject.Singleton
import play.api.Configuration
import play.api.libs.circe.Circe
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{Cookie, InjectedController}
import repo.UserRepo

import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._
// https://developers.google.com/identity/protocols/OAuth2WebServer
@Singleton
class AuthenticationGoogle @Inject() (conf: Configuration, wsClient: WSClient, userRepo: UserRepo)(implicit
    ex: ExecutionContext
) extends InjectedController
    with Circe
    with AuthenticationController {
  val clientId: String     = conf.get[String]("auth.google.clientId")
  val clientSecret: String = conf.get[String]("auth.google.clientSecret")
  val callbackUrl: String  = conf.get[String]("auth.google.callbackUrl")

  private val requestTokenUrl: String =
    Url(scheme = "https", host = "accounts.google.com", path = "/o/oauth2/v2/auth")
      .addParam("client_id" -> clientId)
      .addParam("scope" -> "profile email")
      .addParam("response_type" -> "code")
      .addParam("redirect_uri" -> callbackUrl)
      .toStringPunycode

  private def getAccessToken(code: String): Future[AuthResponse] =
    wsClient
      .url("https://www.googleapis.com/oauth2/v4/token")
      .post(
        Map(
          "code"          -> code,
          "client_id"     -> clientId,
          "client_secret" -> clientSecret,
          "redirect_uri"  -> callbackUrl,
          "grant_type"    -> "authorization_code"
        )
      )
      .pipe(checkStatus(200))
      .flatMap { response =>
        io.circe.parser
          .parse(response.body)
          .flatMap(_.as[AuthResponse])
          .toTry
          .pipe(Future.fromTry)
      }

  private def getUserInfo(token: String): Future[GoogleUserInfo] =
    wsClient
      .url("https://www.googleapis.com/oauth2/v2/userinfo")
      .withHttpHeaders("Authorization" -> s"Bearer $token")
      .execute()
      .pipe(checkStatus(200))
      .flatMap { response =>
        io.circe.parser
          .parse(response.body)
          .flatMap(_.as[GoogleUserInfo])
          .toTry
          .pipe(Future.fromTry)
      }

  private def checkStatus(expected: Int)(fut: Future[WSResponse]): Future[WSResponse] =
    fut.flatMap { resp =>
      if (resp.status == expected)
        Future.successful(resp)
      else
        Future.failed(
          new Exception(s"unexpected status code. expected: ${expected}, actual: ${resp.status}")
        )
    }

  def authenticate() = Action.async { implicit request =>
    request.queryString.get("code") match {
      case None =>
        Redirect(requestTokenUrl)
          // todo: consider using the state-parameter to store the return-to
          .addingToSession("return-to" -> request.getQueryString("return-to").getOrElse("/"))
          .pipe(Future.successful)

      case Some(Seq(code)) =>
        for {
          authResponse <- getAccessToken(code)
          userData     <- getUserInfo(authResponse.access_token)
          user     = userRepo.upsertWithGoogleData(userData)
          returnTo = request.session.get("return-to").getOrElse("/")
        } yield Redirect(returnTo)
          .addingToSession("user-id" -> user.id)
          .removingFromSession("return-to")

      case _ => throw new Exception("unexpected")
    }
  }

  def logout() = Action { _ =>
    Redirect("/").withNewSession
  }
}
