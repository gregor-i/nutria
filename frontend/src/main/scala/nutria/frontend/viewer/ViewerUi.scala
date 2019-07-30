package nutria.frontend.viewer

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core.Point
import nutria.core.viewport.Point._
import nutria.frontend.common.Buttons
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomHelper}
import nutria.frontend.{LenseUtils, NutriaService, common}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.dom.{Element, PointerEvent, WheelEvent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.util.{Failure, Success}

object ViewerUi {
  def render(implicit state: ViewerState, update: ViewerState => Unit): VNode =
    tags.div(
      common.Header("Nutria Fractal Explorer"),
      tags.div(
        attrs.className := "action-bar",
        Buttons.edit(
          attrs.className := "button is-primary",
          events.onClick := (() => update(state.copy(edit = Some(state.fractalEntity))))
        ),
        Buttons.save(
          attrs.className := (state.saveProcess.map(_.value) match {
            case Some(None) => "button is-loading"
            case Some(Some(Success(_))) => "button is-success"
            case Some(Some(Failure(_))) => "button is-danger"
            case None => "button"
          }),
          attrs.disabled := state.saveProcess.exists(!_.isCompleted),
          events.onClick := (() =>
            update(state.copy(saveProcess = Some(NutriaService.save(state.fractalEntity).map(_ => state.fractalEntity))))
          ),
        ),
        Buttons.share(),
        Buttons.logSource(
          events.onClick := (() => dom.console.log(FractalRenderer.fragmentShaderSource(state.fractalEntity.program)))
        )
      ),
      renderCanvas,
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
        common.RenderEditFractalEntity(
          fractal = fractal,
          lens = LenseUtils.lookedUp(fractal, ViewerState.editOptional.asSetter),
          footer = Buttons.group(
            Buttons.accept(
              attrs.className := "button is-primary",
              events.onClick := (() => update(state.copy(fractalEntity = fractal, edit = None)))
            ),
            Buttons.cancel(
              events.onClick := (() => update(state.copy(edit = None)))
            )
          )
        )
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
      val translateA = state.fractalEntity.view.A * ((startPosition._1 - event.pageX) / boundingBox.width)
      val translateB = state.fractalEntity.view.B * ((startPosition._2 - event.pageY) / boundingBox.height)
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
      val y = (event.clientY - boundingBox.top) / boundingBox.height
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
