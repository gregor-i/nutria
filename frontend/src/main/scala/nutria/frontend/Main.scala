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
    if (dom.document.location.hostname != "localhost")
      installServiceWorker()

    dom.document.addEventListener[dom.Event](
      "DOMContentLoaded",
      (_: js.Any) => {
        val container = dom.document.getElementsByTagName("nutria-app").item(0)
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
      _ = registration.addEventListener("updatefound", (_: js.Any) => {
        dom.console.debug("new service worker found. page reload!")
        dom.window.location.reload()
      })
    } yield registration)
      .onComplete {
        case Success(_: ServiceWorkerRegistration) =>
          dom.console.log("[Service Worker] registration successful")
        case Failure(_) =>
          dom.console.log("[Service Worker] registration failed")
      }
}
