package nutria.frontend.explorer

import com.raquo.snabbdom.simple.VNode
import io.circe.parser
import io.circe.syntax._
import nutria.core.{DivergingSeries, FractalEntity}
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.{URIUtils, |}


class ExplorerApp(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: ExplorerState): Unit = {
    dom.window.history.replaceState(null, "", ExplorerApp.url(state.fractalEntity))
    node = patch(node, ExplorerUi.render(state, renderState))
  }

  def initialProgram = ExplorerApp.queryDecoded(dom.window.location.search.stripPrefix("?").stripPrefix("state="))
    .getOrElse(FractalEntity(
      program = DivergingSeries.mandelbrot
    ))

  renderState(ExplorerState(fractalEntity = initialProgram))
}

object ExplorerApp {
  def url(fractalEntity: FractalEntity) = "/viewer?state=" + ExplorerApp.queryEncoded(fractalEntity)

  def queryEncoded(fractalProgram: FractalEntity): String = URIUtils.encodeURIComponent(fractalProgram.asJson.noSpaces)
  def queryDecoded(string: String): Option[FractalEntity] =
    parser.parse(URIUtils.decodeURIComponent(string))
      .flatMap(_.as[FractalEntity])
      .toOption
}