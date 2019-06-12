package nutria.frontend

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core.Point
import nutria.core.viewport.Point._
import nutria.data.Defaults
import nutria.frontend.shaderBuilder.{DeriveableIteration, JuliaSetIteration, MandelbrotIteration, TricornIteration}
import nutria.frontend.util.{Hooks, SnabbdomHelper}
import org.scalajs.dom.{Element, PointerEvent, WheelEvent}
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{HTMLSelectElement, MouseEvent, WebGLRenderingContext}
import spire.math.Complex

import scala.scalajs.js


object Ui {
  def render(implicit state: State, update: State => Unit): VNode =
    tags.div(
      renderCanvas,
      renderControls
    )

  def renderCanvas(implicit state: State, update: State => Unit): VNode =
    tags.canvas(
      styles.build[String]("object-fit", "object-fit") := "cover",
      styles.position := "fixed",
      styles.left := "0",
      styles.top := "0",
      styles.width := "100vw",
      styles.height := "100vh",
      styles.zIndex := "-1",
      styles.overflow := "hidden",
      Hooks.insertHook { vnode =>
        val canvas = vnode.elm.get.asInstanceOf[Canvas]
        val boundingBox = canvas.getBoundingClientRect()
        canvas.width = boundingBox.width.toInt
        canvas.height = boundingBox.height.toInt
        val ctx = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]
        FractalRenderer.render(ctx, state)
      },
      Hooks.postPatchHook { (_, newNode) =>
        val canvas = newNode.elm.get.asInstanceOf[Canvas]
        val boundingBox = canvas.getBoundingClientRect()
        canvas.width = boundingBox.width.toInt
        canvas.height = boundingBox.height.toInt
        val ctx = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]
        FractalRenderer.render(ctx, state)
      },
      SnabbdomHelper.seq(canvasMouseEvents)
    )

  private def canvasMouseEvents(implicit state: State, update: State => Unit): Seq[Modifier[VNode, VNodeData]] = {
    val eventToPoint: PointerEvent => Point = event => (event.pageX, event.pageY)

    val startEvent =
      events.build[MouseEvent]("pointerdown") := { event =>
        update(state.copy(dragStartPosition = Some((event.pageX, event.pageY))))
      }

    def endEvent(startPosition: Point): PointerEvent => Unit = { event =>
      val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
      val translateA = state.view.A * ((-event.pageX + startPosition._1) / boundingBox.width)
      val translateB = state.view.B * ((event.pageY - startPosition._2) / boundingBox.height)
      val newView = state.view.translate(translateA + translateB)
      event.target.asInstanceOf[js.Dynamic].style.left = "0px"
      event.target.asInstanceOf[js.Dynamic].style.top = "0px"
      update(state.copy(dragStartPosition = None, view = newView))
    }

    def moveEvent(startPosition: Point): PointerEvent => Unit = { event =>
      val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
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
      val newView = state.view
        .contain(boundingBox.width, boundingBox.height)
        .zoomSteps((x, y), if (steps > 0) -1 else 1)
      update(state.copy(view = newView))
    }

    startEvent +: wheelEvent +: inProgressEvents.getOrElse(Seq.empty)
  }

  def renderControls(implicit state: State, update: State => Unit): VNode =
    tags.div(
      tags.button("reset", events.onClick := (() => update(state.copy(view = Defaults.defaultViewport)))),
      tags.button(s"more iterations (${state.maxIterations})", events.onClick := (() => update(state.copy(maxIterations = state.maxIterations * 2)))),
      tags.button(s"less iterations (${state.maxIterations})", events.onClick := (() => update(state.copy(maxIterations = state.maxIterations / 2)))),
      tags.button("toggle anit aliase", events.onClick := (() => update(state.copy(antiAliase = if (state.antiAliase == 2) 1 else 2)))),
      tags.select(
        tags.option("Mandelbrot"),
        tags.option("JuliaSet"),
        tags.option("Tricorn"),
        events.onChange := { event =>
          val value = event.target.asInstanceOf[HTMLSelectElement].value
          val newIteration = value match {
            case "Mandelbrot" => MandelbrotIteration
            case "JuliaSet" =>
              val view = state.view
              val center = view.origin + view.A * 0.5 + view.B * 0.5
              JuliaSetIteration(Complex(center._1, center._2))
            case "Tricorn" => TricornIteration
          }
          update(state.copy(iteration = newIteration, shaded = newIteration.isInstanceOf[DeriveableIteration] && state.shaded))
        }
      ),
      tags.button("toggle shaded", attrs.disabled := !state.iteration.isInstanceOf[DeriveableIteration], events.onClick := (() => update(state.copy(shaded = !state.shaded)))),
    )
}
