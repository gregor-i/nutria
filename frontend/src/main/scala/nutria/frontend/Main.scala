package nutria.frontend

import nutria.frontend.library.LibraryApp
import nutria.frontend.explorer.ExplorerApp
import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

object Main {
  def container: Element = dom.document.getElementById("nutria-app")

  def gotoExplorer() =
    new ExplorerApp(container)

  def gotoLibrary() =
    new LibraryApp(container)

  def main(args: Array[String]): Unit = {
    dom.window.location.pathname match {
      case "/viewer" => dom.document.addEventListener[Event]("DOMContentLoaded", (_: Event) => gotoExplorer())
      case "/" => dom.document.addEventListener[Event]("DOMContentLoaded", (_: Event) => gotoLibrary())
      case _ => println("unknown path")
    }
  }
}
