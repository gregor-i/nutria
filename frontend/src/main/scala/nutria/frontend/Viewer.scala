package nutria
package frontend

import com.raquo.snabbdom.simple.VNode
import io.circe.parser
import io.circe.syntax._
import nutria.frontend.shaderBuilder.FractalProgram
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.{URIUtils, |}


class Viewer(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: ViewerState): Unit = {
    dom.window.history.replaceState(null, "", "/viewer?state=" + URIUtils.encodeURIComponent(state.asJson.noSpaces))
    node = patch(node, ViewerUi.render(state, renderState))
  }

  def initialProgram = FractalProgram.queryDecoded(dom.window.location.search.stripPrefix("?").stripPrefix("state="))
      .getOrElse(FractalProgram())

  renderState(ViewerState(fractalProgram = initialProgram))
}
