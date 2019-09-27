package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    def container: Element = dom.document.getElementById("nutria-app")

    val viewerUrlRegex = "/explorer/([0-9a-f]+)".r

    val stateFuture: Future[NutriaState] = dom.window.location.pathname match {
      case "/library" =>
        NutriaService.loadFractals().map(fractals => LibraryState(fractals = fractals))
      case viewerUrlRegex(fractalId) =>
        for {
          fractals <- NutriaService.loadFractals()
          fractal = fractals.find(_.id == fractalId).get
        } yield ExplorerState(fractals = fractals, initialEntity = fractal, fractalEntity = fractal.entity)
      case "/admin" =>
        Admin.setup()
        Future.failed(new Exception)
      case _ =>
        Future.failed(new Exception("unknown url"))
    }


    dom.document.addEventListener[Event]("DOMContentLoaded", (_: js.Any) =>
      stateFuture.onComplete {
        case Success(state) => new nutria.frontend.NutriaApp(container, state)
        case Failure(exception) => println(exception)
      }
    )
  }
}
