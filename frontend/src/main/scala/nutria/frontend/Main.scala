package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.experimental.serviceworkers.{ServiceWorkerContainer, ServiceWorkerRegistration}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.util.{Failure, Success}

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

  private def installServiceWorker(): Unit =
    (for {
      navigator <- Future {
        Dynamic.global.navigator.serviceWorker.asInstanceOf[ServiceWorkerContainer]
      }.filter(!js.isUndefined(_))
      registration <- navigator.register("/assets/sw.js", Dynamic.literal(scope = "/")).toFuture
    } yield registration)
      .onComplete {
        case Success(_: ServiceWorkerRegistration) =>
          dom.console.log("[Service Worker] registration successful")
        case Failure(_) =>
          dom.console.log("[Service Worker] registration failed")
      }
}
