package nutria
package frontend

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple.{VNode, attrs, styles, tags}
import io.circe.{Decoder, parser}
import nutria.data.FractalProgram
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomApp, SnabbdomHelper}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.{Element, XMLHttpRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.|

class Lobby(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: LobbyState): Unit = {
    node = patch(node, LobbyUi.render(state, renderState))
  }

  LobbyService.loadFractals()
    .foreach(fractals => renderState(LobbyState(fractals)))
}

case class LobbyState(programs: Vector[FractalProgram])

object LobbyService {
  def loadFractals(): Future[Vector[FractalProgram]] =
    Ajax.get(s"/api/fractals")
      .map(checkAndParse[Vector[FractalProgram]](200))

  private def checkAndParse[A: Decoder](expected: Int)(req: XMLHttpRequest): A =
    parser.parse(req.responseText).flatMap(_.as[A]).right.get

}

object LobbyUi extends SnabbdomHelper {
  def render(state: LobbyState, update: LobbyState => Unit): VNode = {
    tags.div(
      tags.h1("Nutria Web Viewer"),
      tags.div(
        styles.display := "flex",
        styles.flexWrap := "wrap",
        seqNode(state.programs.map(renderProgramTile)),
        seqNode(Seq.fill(5)(dummyTile))
      )
    )
  }

  def renderProgramTile(fractalProgram: FractalProgram): VNode =
    tags.a(
      styles.flexGrow := "1",
      styles.boxShadow := "rgba(0, 0, 0, 0.32) 8px 8px 8px 0px",
      styles.margin := "8px",
      styles.border := "1px black solid",
      attrs.href := Viewer.url(fractalProgram),
      tags.canvas(
        attrs.widthAttr := 400,
        attrs.heightAttr := 225,
        styles.width := "100%",
        styles.display := "block",
        Hooks.insertHook { node =>
          val canvas = node.elm.get.asInstanceOf[Canvas]
          FractalRenderer.render(canvas, fractalProgram, false)
        }
      )
    )

  val dummyTile =
    tags.span(
      styles.flexGrow := "1",
      styles.marginLeft := "8px",
      styles.marginRight := "8px",
      styles.height := "0px",
      tags.div(
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 225,
          styles.display := "block",
        )
      )
    )
}
