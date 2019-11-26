package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    def container: Element = dom.document.body

    val queryParams = dom.window.location.search
      .dropWhile(_ == '?')
      .split('&')
      .collect {
        case s"${key}=${value}" => key -> value
      }
      .toMap

    val stateFuture: Future[NutriaState] =
      for {
        user <- NutriaService.whoAmI()
        state <- dom.window.location.pathname match {
          case "/library" =>
            NutriaState.libraryState()

          case "/explorer" =>
            Future.successful {
              (for {
                state <- queryParams.get("state")
                fractal <- NutriaApp.queryDecoded(state)
              } yield ExplorerState(user, fractal)
                ).getOrElse(ErrorState(user, "Query Parameter is invalid"))
            }

          case s"/details/${fractalsId}" =>
            for{
              remoteFractal <- NutriaService.loadFractal(fractalsId)
            } yield DetailsState(user, remoteFractal, remoteFractal.entity) // todo: load from query as fallback

          case "/admin" =>
            Admin.setup()
            return

          case _ =>
            Future.successful {
              ErrorState(user, "Unkown url")
            }
        }
      } yield state


    dom.document.addEventListener[Event]("DOMContentLoaded", (_: js.Any) =>
      new nutria.frontend.NutriaApp(container, LoadingState(stateFuture))
    )
  }
}
