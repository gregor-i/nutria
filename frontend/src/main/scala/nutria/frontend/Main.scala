package nutria.frontend

import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.experimental.serviceworkers.{ServiceWorkerContainer, ServiceWorkerNavigator}

import scala.scalajs.js.{Dynamic, UndefOr, |}
import scala.util.{Failure, Success, Try}
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

  private def installServiceWorker(): Unit =
    (for {
      navigator <- Future.fromTry {
        dom.window.navigator
          .asInstanceOf[Dynamic]
          .serviceWorker
          .asInstanceOf[UndefOr[ServiceWorkerNavigator]]
          .fold[Try[ServiceWorkerContainer]](Failure(new Exception))(sw => Success(sw.serviceWorker))
      }
      registration <- navigator.register("/assets/sw.js", Dynamic.literal(scope = "/")).toFuture
      _            <- registration.update.toFuture
    } yield ()).onComplete {
      case Success(_) =>
        dom.console.log("[Service Worker] registration successful")
      case Failure(_) =>
        dom.console.log("[Service Worker] registration failed")
    }
}
