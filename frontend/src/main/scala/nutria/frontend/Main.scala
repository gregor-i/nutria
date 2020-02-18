package nutria.frontend

import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.experimental.serviceworkers.toServiceWorkerNavigator

import scala.scalajs.js.Dynamic
import scala.util.{Failure, Success}
import scala.util.chaining._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    installServiceWorker()

    dom.document.addEventListener[dom.Event](
      "DOMContentLoaded",
      (_: js.Any) => {
        val container = dom.document.createElement("nutria-app")
        dom.document.body.appendChild(container)
        new nutria.frontend.NutriaApp(container)
      }
    )
  }

  private def installServiceWorker() =
    (for {
      navigator <- Future {
        dom.window.navigator
          .pipe(toServiceWorkerNavigator)
          .serviceWorker
      }
      registration <- navigator.register("/js/sw.js", Dynamic.literal(scope = "/")).toFuture
      _            <- registration.update.toFuture
    } yield ()).onComplete {
      case Success(_) =>
        dom.console.log("[Service Worker] registration successful")
      case Failure(_) =>
        dom.console.log("[Service Worker] registration failed")
    }
}
