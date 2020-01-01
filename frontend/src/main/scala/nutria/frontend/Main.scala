package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

import scala.scalajs.js

object Main {
  def main(args: Array[String]): Unit =
    dom.document.addEventListener[Event](
      "DOMContentLoaded",
      (_: js.Any) => new nutria.frontend.NutriaApp(dom.document.getElementById("nutria-app"))
    )
}
