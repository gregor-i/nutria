package controller

import io.circe.Json
import io.circe.generic.auto._
import io.lemonlabs.uri.Url
import play.api.libs.circe.Circe
import play.api.libs.ws.WSClient
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._

class Authenication(clientId: String, clientSecret: String, callbackUrl: String, wsClient: WSClient)
                   (implicit ex: ExecutionContext) extends InjectedController with Circe {
  // https://developers.google.com/identity/protocols/OAuth2WebServer


  val requestTokenUrl: String =
    Url(scheme = "https", host = "accounts.google.com", path = "/o/oauth2/v2/auth")
      .addParam("client_id" -> clientId)
      .addParam("scope" -> "profile email")
      .addParam("response_type" -> "code")
      .addParam("redirect_uri" -> callbackUrl)
      .toStringPunycode

  def getAccessToken(code: String): Future[AuthResponse] =
    wsClient.url("https://www.googleapis.com/oauth2/v4/token")
      .post(Map(
        "code" -> code,
        "client_id" -> clientId,
        "client_secret" -> clientSecret,
        "redirect_uri" -> "http://localhost:9000/auth/google",
        "grant_type" -> "authorization_code"
      ))
      .filter(_.status == 200)
      .flatMap { response =>
        io.circe.parser.parse(response.body)
          .flatMap(_.as[AuthResponse])
          .toTry
          .pipe(Future.fromTry)
      }

  def getUserInfo(token: String): Future[Json] =
    wsClient.url("https://www.googleapis.com/oauth2/v2/userinfo")
      .withHttpHeaders("Authorization" -> s"Bearer $token")
      .execute()
      .filter(_.status == 200)
      .flatMap { response =>
        io.circe.parser.parse(response.body)
          .toTry
          .pipe(Future.fromTry)
      }


  def authenticate() = Action { _ =>
    Redirect(requestTokenUrl)
  }

  def callback(provider: String) = Action.async { request =>
    request.queryString.get("code") match {
      case Some(Seq(code)) =>
        for {
          authResponse <- getAccessToken(code)
          userData <- getUserInfo(authResponse.access_token)
          _ = println(userData)
        } yield Redirect("/") //.withSession(userData.spaces4)
      case None => Future.successful(BadRequest)
    }
  }
}

case class AuthResponse(access_token: String, expires_in: Int, token_type: String)
