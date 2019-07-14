package nutria.frontend.viewer

import com.raquo.snabbdom.simple.VNode
import io.circe.parser
import io.circe.syntax._
import nutria.core.{FractalEntity, Mandelbrot}
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.{URIUtils, |}


class Viewer(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: ViewerState): Unit = {
    dom.window.history.replaceState(null, "", Viewer.url(state.fractalEntity))
    node = patch(node, ViewerUi.render(state, renderState))
  }

  def initialProgram = Viewer.queryDecoded(dom.window.location.search.stripPrefix("?").stripPrefix("state="))
    .getOrElse(FractalEntity(
      description = "",
      reference = None,
      Mandelbrot()
    ))

  renderState(ViewerState(fractalEntity = initialProgram))
}

object Viewer {
  def url(fractalProgram: FractalEntity) = "/viewer?state=" + Viewer.queryEncoded(fractalProgram)

  def queryEncoded(fractalProgram: FractalEntity): String = URIUtils.encodeURIComponent(fractalProgram.asJson.noSpaces)
  def queryDecoded(string: String): Option[FractalEntity] =
    parser.parse(URIUtils.decodeURIComponent(string))
      .flatMap(_.as[FractalEntity])
      .toOption
}