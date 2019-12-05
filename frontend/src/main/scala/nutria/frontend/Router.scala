package nutria.frontend

import io.circe.{Decoder, Encoder}
import io.circe.syntax._
import nutria.core.{FractalEntity, FractalImage}
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object Router {
  def stateFromUrl(location: dom.Location): Future[NutriaState] = {
    val queryParams = location.search
      .dropWhile(_ == '?')
      .split('&')
      .collect {
        case s"${key}=${value}" => key -> value
      }
      .toMap

    for {
      user <- NutriaService.whoAmI()
      state <- location.pathname match {
        case "/" =>
          for {
            remoteFractals <- NutriaService.loadPublicFractals()
            allImages = FractalImage.allImages(remoteFractals.map(_.entity))
            randomFractal = allImages((Math.random() * allImages.length).toInt) // todo: the service should provide an endpoint for this
          } yield GreetingState(randomFractal)

        case "/library" =>
          NutriaState.libraryState()

        case s"/fractals/${fractalsId}/details" =>
          for {
            remoteFractal <- NutriaService.loadFractal(fractalsId)
          } yield DetailsState(user, remoteFractal, remoteFractal.entity)

        case s"/fractals/${fractalId}/explorer" =>
          for {
            remoteFractal <- NutriaService.loadFractal(fractalId)
            image = FractalImage(remoteFractal.entity.program, remoteFractal.entity.views.value.head, remoteFractal.entity.antiAliase)
          } yield ExplorerState(user, Some(remoteFractal.id), image)

        case "/explorer" =>
          Future.successful {
            (for {
              state <- queryParams.get("state")
              fractal <- queryDecoded[FractalImage](state)
            } yield ExplorerState(user, None, fractal)
              ).getOrElse(ErrorState("Query Parameter is invalid"))
          }

        case _ =>
          Future.successful {
            ErrorState("Unkown url")
          }
      }
    } yield state
  }

  def stateToUrl(state: NutriaState): Option[(String, Map[String, String])] = state match {
    case _: LibraryState =>
      Some(("/library", Map.empty))
    case details: DetailsState =>
      Some((s"/fractals/${details.remoteFractal.id}/details", Map("fractal" -> queryEncoded(details.fractal))))
    case exState: ExplorerState =>
      exState.fractalId match {
        case Some(fractalId) => Some((s"/fractals/${fractalId}/explorer", Map("state" -> queryEncoded(exState.fractalImage))))
        case None => Some((s"/explorer", Map("state" -> queryEncoded(exState.fractalImage))))
      }
    case _: GreetingState =>
      Some(("/", Map.empty))
    case _: ErrorState =>
      None
    case _: LoadingState =>
      None
  }

  def queryEncoded[T : Encoder](t: T): String =
    dom.window.btoa(t.asJson.noSpaces)

  def queryDecoded[T : Decoder](string: String): Option[T] =
    (for {
      decoded <- Try(dom.window.atob(string)).toEither
      json <- io.circe.parser.parse(new String(decoded))
      decoded <- json.as[T]
    } yield decoded).toOption
}