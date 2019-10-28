package controller

import io.circe.Json
import io.circe.generic.auto._
import io.lemonlabs.uri.Url
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.circe.Circe
import play.api.libs.ws.WSClient
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}


@Singleton()
class Authenication @Inject()(wsClient: WSClient, conf: Configuration)(implicit ex: ExecutionContext) extends InjectedController with Circe {
  // https://developers.google.com/identity/protocols/OAuth2WebServer

  val clientId: String = conf.get[String]("auth.google.clientId")
  val clientSecret: String = conf.get[String]("auth.google.clientSecret")

  def requestTokenUrl(): String =
    Url(scheme = "https", host = "accounts.google.com", path = "/o/oauth2/v2/auth")
      .addParam("client_id" -> clientId)
      .addParam("scope" -> "profile email")
      .addParam("response_type" -> "code")
      .addParam("redirect_uri" -> "http://localhost:9000/auth/google")
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
        println("https://oauth2.googleapis.com/token", response.body)

        io.circe.parser.parse(response.body).flatMap(_.as[AuthResponse]) match {
          case Right(json) => Future.successful(json)
          case Left(failure) => Future.failed(failure)
        }
      }

  def getUserInfo(token: String): Future[Json] =
    wsClient.url("https://www.googleapis.com/oauth2/v2/userinfo")
      .withHttpHeaders("Authorization" -> s"Bearer $token")
      .execute()
      .filter(_.status == 200)
      .flatMap { response =>
        io.circe.parser.parse(response.body) match {
          case Right(json) => Future.successful(json)
          case Left(failure) => Future.failed(failure)
        }
      }


  def authenticate() = Action { _ =>
    Redirect(requestTokenUrl())
  }

  def callback(provider: String) = Action.async { request =>
    request.queryString.get("code") match {
      case Some(Seq(code)) =>
        for {
          authResponse <- getAccessToken(code)
          userData <- getUserInfo(authResponse.access_token)
        } yield Ok(userData.spaces4)
      case None => Future.successful(BadRequest)
    }
  }
}

case class AuthResponse(access_token: String, expires_in: Int, token_type: String)
