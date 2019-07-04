package nutria
package frontend

import com.raquo.snabbdom.simple.VNode
import io.circe.parser
import io.circe.syntax._
import nutria.core.{FractalProgram, Mandelbrot}
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.{URIUtils, |}


class Viewer(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: ViewerState): Unit = {
    dom.window.history.replaceState(null, "", Viewer.url(state.fractalProgram))
    node = patch(node, ViewerUi.render(state, renderState))
  }

  def initialProgram = Viewer.queryDecoded(dom.window.location.search.stripPrefix("?").stripPrefix("state="))
      .getOrElse(Mandelbrot())

  renderState(ViewerState(fractalProgram = initialProgram))
}

object Viewer{
  def url(fractalProgram: FractalProgram) = "/viewer?state=" + Viewer.queryEncoded(fractalProgram)

  def queryEncoded(fractalProgram: FractalProgram): String = URIUtils.encodeURIComponent(fractalProgram.asJson.noSpaces)
  def queryDecoded(string: String): Option[FractalProgram] =
    parser.parse(URIUtils.decodeURIComponent(string))
      .flatMap(_.as[FractalProgram])
      .toOption
}