package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

import scala.scalajs.js
import org.scalajs.dom.experimental.serviceworkers.toServiceWorkerNavigator

import scala.scalajs.js.Dynamic
import scala.util.{Failure, Success}
import scala.util.chaining._

import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  def main(args: Array[String]): Unit = {
    dom.window.navigator
      .pipe(toServiceWorkerNavigator)
      .serviceWorker
      .register("/js/sw.js", Dynamic.literal(scope = "/"))
      .toFuture
      .onComplete {
        case Success(registration) =>
          dom.console.log("[Service Worker] registration successful")
          registration.update
        case Failure(_) =>
          dom.console.log("[Service Worker] registration failed")
      }

    dom.document.addEventListener[Event](
      "DOMContentLoaded",
      (_: js.Any) => {
        val container = dom.document.createElement("nutria-app")
        dom.document.body.appendChild(container)
        new nutria.frontend.NutriaApp(container)
      }
    )
  }
}
