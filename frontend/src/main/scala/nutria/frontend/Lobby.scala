package nutria
package frontend

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple.{VNode, attrs, styles, tags}
import io.circe.{Decoder, parser}
import nutria.data.{FractalEntity, FractalProgram}
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

case class LobbyState(programs: Vector[FractalEntity])

object LobbyService {
  def loadFractals(): Future[Vector[FractalEntity]] =
    Ajax.get(s"/api/fractals")
      .map(checkAndParse[Vector[FractalEntity]](200))

  private def checkAndParse[A: Decoder](expected: Int)(req: XMLHttpRequest): A =
    parser.parse(req.responseText).flatMap(_.as[A]).right.get

}

object LobbyUi extends SnabbdomHelper {
  def render(state: LobbyState, update: LobbyState => Unit): VNode = {
    tags.div(
      header,
      tags.div(
        attrs.className := "lobby-tile-list",
        seqNode(state.programs.map(renderProgramTile)),
        seqNode(Seq.fill(5)(dummyTile))
      )
    )
  }

  def header: VNode =
    tags.div(
      attrs.className := "top-bar",
      tags.div(
        tags.img(
          styles.height := "100%",
          attrs.src := "/img/icon.png",
        ),
        tags.span(
          "Nutria Fractal Library"
        )
      )
    )

  def renderProgramTile(fractal: FractalEntity): VNode =
    tags.div(
      tags.a(
        attrs.href := Viewer.url(fractal.program),
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 225,
          Hooks.insertHook { node =>
            val canvas = node.elm.get.asInstanceOf[Canvas]
            FractalRenderer.render(canvas, fractal.program, false)
          }
        )
      ),
      attrs.title := fractal.description,
      //      Option(fractal.description).filter(_ != "").map(text =>
      //        tags.div(
      //          styles.padding := "1.5rem",
      //          text
      //        )
      //      ),
      tags.footer(
        tags.a(
          attrs.href := Viewer.url(fractal.program),
          "explore",
        ),
        tags.a(
          fractal.reference.map(url => attrs.href := url),
          "documenation"
        ),
        tags.a(
          "delete"
        )
      )
    )

  val dummyTile =
    tags.div(
      attrs.className := "dummy-tile",
      tags.div(
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 0,
          styles.display := "block",
        )
      )
    )
}
