package nutria.frontend

import com.raquo.snabbdom.simple.VNode
import io.circe.syntax._
import nutria.core.FractalEntity
import nutria.frontend.explorer.ExplorerUi
import nutria.frontend.library.LibraryUi
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.{URIUtils, |}
import scala.util.Try


class NutriaApp(container: Element, initialState: NutriaState) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: NutriaState): Unit = {
    val (currentPath, currentSearch) = NutriaApp.url(state)
    if (dom.window.location.pathname != currentPath) {
      dom.window.history.pushState(state.asJson.noSpaces, "", currentPath + "?" + currentSearch)
    } else if (dom.window.location.search != currentSearch) {
      dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath + "?" + currentSearch)
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
  def url(state: NutriaState): (String, String) = state match {
    case libState: LibraryState if libState.edit.isEmpty => ("/library", "")
    case libState: LibraryState if libState.edit.isDefined => ("/library", s"details=${libState.edit.get.id}")
    case exState: ExplorerState => (s"/explorer/${exState.initialEntity.id}", "state=" + NutriaApp.queryEncoded(exState.fractalEntity))
  }

  def queryEncoded(fractalProgram: FractalEntity): String = URIUtils.encodeURIComponent(fractalProgram.asJson.noSpaces)
}