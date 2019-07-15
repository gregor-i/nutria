package nutria.frontend.library

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core._
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomHelper}
import nutria.frontend.viewer.Viewer
import nutria.frontend.{LenseUtils, NutriaService, common}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLElement

import scala.concurrent.ExecutionContext.Implicits.global

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
        common.RenderEditFractalEntity(
          fractal = fractal,
          lens = LenseUtils.lookedUp(fractal, LibraryState.editOptional.asSetter),
          buttons = Seq(
            tags.button(
              attrs.className := "button is-link",
              events.onClick := (() => dom.window.location.assign(Viewer.url(fractal))),
              "explore"
            ),
            tags.button(
              attrs.className := "button",
              events.onClick := {event =>
                event.target.asInstanceOf[HTMLElement].classList.add("is-loading")
                NutriaService.save(fractal)
                  .foreach{ newFractals =>
                    event.target.asInstanceOf[HTMLElement].classList.remove("is-loading")
                    event.target.asInstanceOf[HTMLElement].classList.add("is-success")
                    update(LibraryState(
                      programs = newFractals
                    ))
                  }
              },
              "save"
            )
          ))
      )
    }

  def renderProgramTile(fractal: FractalEntity)
                       (implicit state: LibraryState, update: LibraryState => Unit): VNode =
    tags.build("article")(
      events.onClick := (() => update(state.copy(edit = Some(fractal)))),
      tags.canvas(
        attrs.widthAttr := 400,
        attrs.heightAttr := 225,
        Hooks.insertHook { node =>
          val canvas = node.elm.get.asInstanceOf[Canvas]
          FractalRenderer.render(canvas, fractal.program, false)
        }
      ),
      attrs.title := fractal.description
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
