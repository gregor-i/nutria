package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

import scala.scalajs.js

object Main {

  def main(args: Array[String]): Unit = {
    if (dom.window.location.pathname == "/admin") {
      Admin.setup()
      return
    }

    def container: Element = dom.document.body

    dom.document.addEventListener[Event]("DOMContentLoaded", (_: js.Any) =>
      new nutria.frontend.NutriaApp(container)
    )
  }
}
