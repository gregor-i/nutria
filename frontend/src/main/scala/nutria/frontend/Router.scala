package nutria.frontend

import io.circe.syntax._
import nutria.core.{DivergingSeries, FractalEntity}
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Random, Try}

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
            randomFractal = remoteFractals((Math.random()*remoteFractals.length).toInt)
          } yield GreetingState(randomFractal.entity)

        case "/library" =>
          NutriaState.libraryState()

        case s"/fractals/${fractalsId}/details" =>
          for {
            remoteFractal <- NutriaService.loadFractal(fractalsId)
          } yield DetailsState(user, remoteFractal, remoteFractal.entity)

        case s"/fractals/${fractalId}/explorer" =>
          for{
            remoteFractal <- NutriaService.loadFractal(fractalId)
          } yield ExplorerState(user, Some(remoteFractal.id), remoteFractal.entity)

        case "/explorer" =>
          Future.successful {
            (for {
              state <- queryParams.get("state")
              fractal <- queryDecoded(state)
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
    case ExplorerState(_, Some(fractalId), fractal)=>
      Some((s"/fractals/${fractalId}/explorer", Map("state" -> queryEncoded(fractal))))
    case exState: ExplorerState =>
      Some((s"/explorer", Map("state" -> queryEncoded(exState.fractalEntity))))
    case _: GreetingState =>
      Some(("/", Map.empty))
    case _: ErrorState =>
      None
    case _: LoadingState =>
      None
  }

  def queryEncoded(fractalProgram: FractalEntity): String =
    dom.window.btoa(fractalProgram.asJson.noSpaces)

  def queryDecoded(string: String): Option[FractalEntity] =
    (for {
      decoded <- Try(dom.window.atob(string)).toEither
      json <- io.circe.parser.parse(new String(decoded))
      decoded <- json.as[FractalEntity]
    } yield decoded).toOption
}