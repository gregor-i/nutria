package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

object Main {
  def container: Element = dom.document.getElementById("nutria-app")

  def gotoViewer() =
    new Viewer(container)

  def gotoLobby() =
    new Lobby(container)


  def main(args: Array[String]): Unit = {
    dom.window.location.pathname match {
      case "/viewer" => dom.document.addEventListener[Event]("DOMContentLoaded", (_: Event) => gotoViewer())
      case "/"       => dom.document.addEventListener[Event]("DOMContentLoaded", (_: Event) => gotoLobby())
      case _         => println("unknown path")
    }
  }
}
