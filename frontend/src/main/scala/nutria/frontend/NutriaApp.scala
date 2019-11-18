package nutria.frontend

import io.circe.syntax._
import nutria.core.FractalEntity
import nutria.frontend.ui.{ErrorUi, ExplorerUi, LibraryUi, Ui}
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.VNode

import scala.scalajs.js
import scala.scalajs.js.|
import scala.util.Try

class NutriaApp(container: Element, initialState: NutriaState) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: NutriaState): Unit = {
    NutriaApp.url(state) match {
      case Some((currentPath, currentSearch)) =>
        val stringSearch = currentSearch.map {
          case (key, value) => s"$key=$value"
        }.mkString("&")
        if (dom.window.location.pathname != currentPath) {
          if (currentSearch.nonEmpty)
            dom.window.history.pushState(state.asJson.noSpaces, "", currentPath + "?" + stringSearch)
          else
            dom.window.history.pushState(state.asJson.noSpaces, "", currentPath)
        } else if (dom.window.location.search != stringSearch) {
          if (currentSearch.nonEmpty)
            dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath + "?" + stringSearch)
          else
            dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath)
        }
      case None => ()
    }

    node = patch(node, Ui(state, renderState))
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
  def url(state: NutriaState): Option[(String, Map[String, String])] = state match {
    case _: LibraryState => Some(("/library", Map.empty))
    case exState: ExplorerState => Some((s"/explorer", Map("state" -> NutriaApp.queryEncoded(exState.fractalEntity))))
    case details: DetailsState => Some((s"/details/${details.remoteFractal.id}", Map("fractal" -> NutriaApp.queryEncoded(details.fractal))))
    case _: ErrorState => None
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