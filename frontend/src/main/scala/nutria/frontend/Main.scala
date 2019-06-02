package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

object Main {
  def container: Element = dom.document.getElementById("nutria-app")

  def gotoApp() ={
    new App(container)
  }



  def main(args: Array[String]): Unit = {
    dom.window.location.pathname match {
      case "/" => dom.document.addEventListener[Event]("DOMContentLoaded", (_: Event) => gotoApp())
      case _ => println("unknown path")
    }
  }
}
