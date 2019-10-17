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

    val stateFuture: Future[NutriaState] = dom.window.location.pathname match {
      case "/library" =>
        for {
          fractals <- NutriaService.loadFractals()
          edit = queryParams.get("details").flatMap(d => fractals.find(_.id == d))
          tab = queryParams.get("tab").flatMap(Tab.fromString).getOrElse(Tab.default)
        } yield LibraryState(fractals = fractals, edit = edit, tab = tab)

      case s"/explorer" =>
        Future.successful {
          (for {
            state <- queryParams.get("state")
            fractal <- NutriaApp.queryDecoded(state)
          } yield ExplorerState(fractal)
            ).getOrElse(ErrorState("Query Parameter is invalid"))
        }

      case "/admin" =>
        Admin.setup()
        Future.failed(new Exception)

      case _ =>
        Future.successful{
          ErrorState("Unkown url")
        }
    }


    dom.document.addEventListener[Event]("DOMContentLoaded", (_: js.Any) =>
      stateFuture.onComplete {
        case Success(state) => new nutria.frontend.NutriaApp(container, state)
        case Failure(exception) => println(exception)
      }
    )
  }
}
