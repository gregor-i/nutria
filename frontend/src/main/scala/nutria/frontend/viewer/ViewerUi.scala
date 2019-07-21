package nutria.frontend.viewer

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core.Point
import nutria.core.viewport.Point._
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomHelper}
import nutria.frontend.{LenseUtils, common}
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.dom.{Element, PointerEvent, WheelEvent}

import scala.scalajs.js

object ViewerUi {
  def render(implicit state: ViewerState, update: ViewerState => Unit): VNode =
    tags.div(
      common.Header("Nutria Fractal Explorer"),
      renderCanvas,
      tags.a("edit",
        styles.background := "white",
        events.onClick := (() => update(state.copy(edit = Some(state.fractalEntity))))
      ),
      renderPopup()
    )

  def renderPopup()
                 (implicit state: ViewerState, update: ViewerState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      tags.div(
        attrs.className := "modal is-active",
        tags.div(
          attrs.className := "modal-background",
          events.onClick := (() => update(state.copy(edit = None))),
        ),
        common.RenderEditFractalEntity(fractal, LenseUtils.lookedUp(fractal, ViewerState.editOptional.asSetter), Seq.empty)
      )
    }

  def renderCanvas(implicit state: ViewerState, update: ViewerState => Unit): VNode =
    tags.canvas(
      attrs.className := "full-size",
      Hooks.insertHook { vnode => FractalRenderer.render(vnode.elm.get.asInstanceOf[Canvas], state.fractalEntity, true) },
      Hooks.postPatchHook { (_, newNode) => FractalRenderer.render(newNode.elm.get.asInstanceOf[Canvas], state.fractalEntity, true) },
      SnabbdomHelper.seq(canvasMouseEvents)
    )

  private def canvasMouseEvents(implicit state: ViewerState, update: ViewerState => Unit): Seq[Modifier[VNode, VNodeData]] = {
    val startEvent =
      events.build[MouseEvent]("pointerdown") := { event =>
        update(state.copy(dragStartPosition = Some((event.pageX, event.pageY))))
      }

    def endEvent(startPosition: Point): PointerEvent => Unit = { event =>
      val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
      val translateA = state.fractalEntity.view.A * ((-event.pageX + startPosition._1) / boundingBox.width)
      val translateB = state.fractalEntity.view.B * ((event.pageY - startPosition._2) / boundingBox.height)
      val newView = state.fractalEntity.view.translate(translateA + translateB)
      event.target.asInstanceOf[js.Dynamic].style.left = "0px"
      event.target.asInstanceOf[js.Dynamic].style.top = "0px"
      update(ViewerState.viewport.set(newView)(state).copy(dragStartPosition = None))
    }

    def moveEvent(startPosition: Point): PointerEvent => Unit = { event =>
      val left = event.pageX - startPosition._1
      val top = event.pageY - startPosition._2
      event.target.asInstanceOf[js.Dynamic].style.left = s"${left}px"
      event.target.asInstanceOf[js.Dynamic].style.top = s"${top}px"
    }

    val inProgressEvents = state.dragStartPosition.map { startPosition =>
      Seq(
        events.build[PointerEvent]("pointerup") := endEvent(startPosition),
        events.build[PointerEvent]("pointercancel") := endEvent(startPosition),
        events.build[PointerEvent]("pointerout") := endEvent(startPosition),
        events.build[PointerEvent]("pointermove") := moveEvent(startPosition),
      )
    }

    val wheelEvent = events.onWheel := { event =>
      val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
      val x = (event.clientX - boundingBox.left) / boundingBox.width
      val y = 1 - (event.clientY - boundingBox.top) / boundingBox.height
      val steps = event.asInstanceOf[WheelEvent].deltaY

      update(
        ViewerState.viewport.modify {
          _.contain(boundingBox.width, boundingBox.height)
            .zoomSteps((x, y), if (steps > 0) -1 else 1)
        }(state)
      )
    }

    startEvent +: wheelEvent +: inProgressEvents.getOrElse(Seq.empty)
  }
}
