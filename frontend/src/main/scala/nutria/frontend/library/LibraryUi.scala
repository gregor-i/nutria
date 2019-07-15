package nutria.frontend.library

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core._
import nutria.frontend.{LenseUtils, common}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomHelper}
import nutria.frontend.viewer.Viewer
import org.scalajs.dom.html.Canvas

object LibraryUi extends SnabbdomHelper {
  def render(implicit state: LibraryState, update: LibraryState => Unit): VNode = {
    tags.div(
      common.Header("Nutria Fractal Library"),
      tags.div(
        attrs.className := "lobby-tile-list",
        seqNode(state.programs.map(renderProgramTile)),
        seqNode(Seq.fill(5)(dummyTile))
      ),
      renderPopup(),
    )
  }

  def renderPopup()
                 (implicit state: LibraryState, update: LibraryState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      tags.div(
        attrs.className := "modal is-active",
        tags.div(
          attrs.className := "modal-background",
          events.onClick := (() => update(state.copy(edit = None))),
        ),
        tags.div(
          attrs.className := "modal-content lobby-tile-list",
          common.RenderEditFractalEntity(fractal, LenseUtils.lookedUp(fractal, LibraryState.editOptional.asSetter))
        )
      )
    }

  def renderProgramTile(fractal: FractalEntity)
                       (implicit state: LibraryState, update: LibraryState => Unit): VNode =
    tags.build("article")(
      tags.a(
        attrs.href := Viewer.url(fractal),
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
      tags.footer(
        tags.a(
          attrs.href := Viewer.url(fractal),
          "explore",
        ),
        tags.a(
          events.onClick := (() => update(state.copy(edit = Some(fractal)))),
          "edit",
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
    tags.build("article")(
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
