package nutria.frontend

import io.circe.syntax._
import nutria.core.FractalEntity
import nutria.frontend.explorer.ExplorerUi
import nutria.frontend.library.LibraryUi
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.VNode

import scala.scalajs.js
import scala.scalajs.js.{URIUtils, |}
import scala.util.Try


class NutriaApp(container: Element, initialState: NutriaState) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: NutriaState): Unit = {
    val (currentPath, currentSearch) = NutriaApp.url(state)
    val stringSearch = currentSearch.map{case (key, value) => s"$key=$value"} .mkString("&")
    if (dom.window.location.pathname != currentPath) {
      if(currentSearch.nonEmpty)
        dom.window.history.pushState(state.asJson.noSpaces, "", currentPath + "?" + stringSearch)
      else
        dom.window.history.pushState(state.asJson.noSpaces, "", currentPath)
    } else if (dom.window.location.search != stringSearch) {
      if(currentSearch.nonEmpty)
        dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath + "?" + stringSearch)
      else
        dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath)
    }


    val ui = state match {
      case exState: ExplorerState =>
        ExplorerUi.render(exState, renderState)
      case libState: LibraryState =>
        LibraryUi.render(libState, renderState)
    }

    node = patch(node, ui)
  }

  dom.window.onpopstate = event => {
    (for {
      jsonString <- Try(event.state.asInstanceOf[String]).toEither
      json <- io.circe.parser.parse(jsonString)
      decoded <- json.as[NutriaState]
    } yield decoded) match {
      case Right(state) => renderState(state)
      case Left(error) => dom.console.error("unexpected problem in window.onpopstate", error.asInstanceOf[js.Any])
    }
  }

  renderState(initialState)

}

object NutriaApp {
  def url(state: NutriaState): (String, Map[String, String]) = state match {
    case libState: LibraryState if libState.edit.isEmpty => ("/library", Map.empty)
    case libState: LibraryState if libState.edit.isDefined => ("/library", Map("details" -> libState.edit.get.id))
    case exState: ExplorerState => (s"/explorer", Map("state" -> NutriaApp.queryEncoded(exState.fractalEntity)))
  }

  def queryEncoded(fractalProgram: FractalEntity): String =
    dom.window.btoa(fractalProgram.asJson.noSpaces)
  def queryDecoded(string: String): Option[FractalEntity]=
    (for{
      decoded <- Try(dom.window.atob(string)).toEither
      json <- io.circe.parser.parse(decoded)
      decoded <- json.as[FractalEntity]
    } yield decoded).toOption
}