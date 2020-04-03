package nutria.frontend

import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import nutria.core.FractalImage
import nutria.frontend.service.{NutriaAdminService, NutriaService}
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.Dynamic
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
          Links.greetingState()
        )

      case "/gallery" =>
        LoadingState(
          Links.galleryState()
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
          Links.userGalleryState(userId)
        )

      case s"/fractals/${fractalsId}/details" =>
        LoadingState(
          Links.detailsState(fractalsId)
        )

      case s"/fractals/${fractalId}/explorer" =>
        LoadingState(
          for {
            user          <- NutriaService.whoAmI()
            remoteFractal <- NutriaService.loadFractal(fractalId)
          } yield {
            val fractalFromUrl =
              queryParams.get("state").flatMap(queryDecoded[FractalImage])

            fractalFromUrl match {
              case Some(image) =>
                ExplorerState(
                  user,
                  remoteFractal = Some(remoteFractal),
                  fractalImage = image
                )
              case None => ErrorState("Query Parameter is invalid")
            }
          }
        )

      case "/explorer" =>
        LoadingState(
          NutriaService
            .whoAmI()
            .map { user =>
              val fractalFromUrl =
                queryParams.get("state").flatMap(queryDecoded[FractalImage])

              fractalFromUrl match {
                case Some(fractal) => ExplorerState(user, None, fractal)
                case None          => ErrorState("Query Parameter is invalid")
              }
            }
        )

      case "/new-fractal" =>
        val stepFromUrl = queryParams.get("step").flatMap(queryDecoded[CreateNewFractalState.Step])

        LoadingState {
          NutriaService.whoAmI().map { user =>
            stepFromUrl match {
              case Some(step) => CreateNewFractalState(user = user, step = step)
              case None       => ErrorState("Query Parameter is invalid")
            }
          }
        }

      case "/faq" =>
        LoadingState(
          Links.faqState()
        )

      case "/admin" =>
        LoadingState(
          NutriaAdminService.load()
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
    case _: GalleryState =>
      Some(("/gallery", Map.empty))
    case state: UserGalleryState =>
      Some((s"/user/${state.aboutUser}/gallery", Map.empty))
    case details: DetailsState =>
      Some((s"/fractals/${details.remoteFractal.id}/details", Map.empty))
    case exState: ExplorerState =>
      exState.remoteFractal match {
        case Some(remoteFractal) =>
          Some(
            (s"/fractals/${remoteFractal.id}/explorer", Map("state" -> queryEncoded(exState.fractalImage)))
          )
        case None => Some((s"/explorer", Map("state" -> queryEncoded(exState.fractalImage))))
      }
    case _: ProfileState              => Some("/user/profile" -> Map.empty)
    case state: CreateNewFractalState => Some("/new-fractal"  -> Map("step" -> queryEncoded(state.step)))
    case _: GreetingState =>
      Some(("/", Map.empty))
    case _: FAQState   => Some("/faq"   -> Map.empty)
    case _: AdminState => Some("/admin" -> Map.empty)
    case _: ErrorState =>
      None
    case _: LoadingState =>
      None
  }

  def queryEncoded[T: Encoder](t: T): String = {
    val encoded = t.asJson.noSpaces
    if (!js.isUndefined(Dynamic.global.window) && !js.isUndefined(Dynamic.global.window.btoa)) {
      dom.window.btoa(encoded)
    } else {
      // note: only static rendering has this function undefined
      ""
    }
  }

  def queryDecoded[T: Decoder](string: String): Option[T] =
    (for {
      decoded <- Try(dom.window.atob(string)).toEither
      json    <- io.circe.parser.parse(new String(decoded))
      decoded <- json.as[T]
    } yield decoded).toOption
}
