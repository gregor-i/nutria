package nutria.frontend

import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import nutria.api.User
import nutria.frontend.pages.ErrorState
import nutria.frontend.service.NutriaService
import org.scalajs.dom

import scala.util.Try
import scala.util.chaining._

object Router {
  type Path           = String
  type QueryParameter = Map[String, String]
  type Location       = (Path, QueryParameter)

  def stateFromUrl(location: dom.Location, user: Option[User]): NutriaState =
    stateFromUrl((location.pathname, queryParamsFromUrl(location.search)): Location, user)

  private val stateFromUrlPF: ((Option[User], Path, QueryParameter)) => Option[NutriaState] =
    Pages.all
      .map(_.stateFromUrl)
      .reduce(_ orElse _)
      .lift
  def stateFromUrl(location: Location, user: Option[User]): NutriaState =
    stateFromUrlPF((user, location._1, location._2)).getOrElse(ErrorState(user, "Unkown url"))

  def stateToUrl[State <: NutriaState](state: State): Option[Location] =
    Pages.selectPage(state).stateToUrl(state)

  def queryParamsToUrl(search: QueryParameter): String = {
    val stringSearch = search
      .map {
        case (key, value) => s"$key=$value"
      }
      .mkString("&")
    if (stringSearch == "")
      ""
    else
      "?" + stringSearch
  }

  def queryParamsFromUrl(search: String): QueryParameter =
    search
      .dropWhile(_ == '?')
      .split('&')
      .collect {
        case s"${key}=${value}" => key -> value
      }
      .toMap

  def queryEncoded[T: Encoder](t: T): String =
    t.asJson.noSpaces
      .pipe(dom.window.btoa)

  def queryDecoded[T: Decoder](string: String): Option[T] =
    (for {
      decoded <- Try(dom.window.atob(string)).toEither
      json    <- io.circe.parser.parse(new String(decoded))
      decoded <- json.as[T]
    } yield decoded).toOption
}
