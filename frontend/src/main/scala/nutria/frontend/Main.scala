package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.ServiceWorkerRegistrationOptions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.util.{Failure, Success}

object Main {
  def main(args: Array[String]): Unit = {
    dom.document.addEventListener[dom.Event](
      "DOMContentLoaded",
      (_: js.Any) => {
        val container = dom.document.getElementsByTagName("nutria-app").item(0)
        new nutria.frontend.NutriaApp(container)
      }
    )
  }
}
