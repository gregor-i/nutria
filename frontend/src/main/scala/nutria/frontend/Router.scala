package nutria.frontend

import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import nutria.core.FractalImage
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object Router {
  def stateFromUrl(location: dom.Location): NutriaState = {
    val queryParams = location.search
      .dropWhile(_ == '?')
      .split('&')
      .collect {
        case s"${key}=${value}" => key -> value
      }
      .toMap

    location.pathname match {
      case "/" =>
        LoadingState(
          NutriaState.greetingState()
        )

      case "/gallery" =>
        LoadingState(
          NutriaState.libraryState()
        )

      case s"/user/profile" =>
        LoadingState(
          NutriaService.whoAmI().map {
            case Some(user) => ProfileState(about = user)
            case None       => ErrorState("You are not logged in")
          }
        )

      case s"/user/${userId}/gallery" =>
        LoadingState(
          NutriaState.userLibraryState(userId)
        )

      case s"/fractals/${fractalsId}/details" =>
        LoadingState(
          NutriaState.detailsState(fractalsId)
        )

      case s"/fractals/${fractalId}/explorer" =>
        LoadingState(
          for {
            user          <- NutriaService.whoAmI()
            remoteFractal <- NutriaService.loadFractal(fractalId)
            image = FractalImage(
              remoteFractal.entity.program,
              remoteFractal.entity.views.value.head,
              remoteFractal.entity.antiAliase
            )
          } yield ExplorerState(
            user,
            Some(remoteFractal.id),
            owned = user.exists(_.id == remoteFractal.owner),
            image
          )
        )

      case "/explorer" =>
        LoadingState(
          NutriaService
            .whoAmI()
            .map { user =>
              (for {
                state   <- queryParams.get("state")
                fractal <- queryDecoded[FractalImage](state)
              } yield ExplorerState(user, None, owned = false, fractal))
                .getOrElse(ErrorState("Query Parameter is invalid"))
            }
        )

      case _ =>
        ErrorState("Unkown url")
    }
  }

  def searchToUrl(search: Map[String, String]): String = {
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

  def stateToUrl(state: NutriaState): Option[(String, Map[String, String])] = state match {
    case _: LibraryState =>
      Some(("/gallery", Map.empty))
    case state: UserLibraryState =>
      Some((s"/user/${state.aboutUser}/gallery", Map.empty))
    case details: DetailsState =>
      Some((s"/fractals/${details.remoteFractal.id}/details", Map.empty))
    case exState: ExplorerState =>
      exState.fractalId match {
        case Some(fractalId) =>
          Some(
            (s"/fractals/${fractalId}/explorer", Map("state" -> queryEncoded(exState.fractalImage)))
          )
        case None => Some((s"/explorer", Map("state" -> queryEncoded(exState.fractalImage))))
      }
    case _: ProfileState => Some("/user/profile" -> Map.empty)
    case _: GreetingState =>
      Some(("/", Map.empty))
    case _: ErrorState =>
      None
    case _: LoadingState =>
      None
  }

  def queryEncoded[T: Encoder](t: T): String =
    dom.window.btoa(t.asJson.noSpaces)

  def queryDecoded[T: Decoder](string: String): Option[T] =
    (for {
      decoded <- Try(dom.window.atob(string)).toEither
      json    <- io.circe.parser.parse(new String(decoded))
      decoded <- json.as[T]
    } yield decoded).toOption
}
