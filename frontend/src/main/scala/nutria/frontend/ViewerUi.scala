package nutria.frontend

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core.Point
import nutria.core.viewport.Point._
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomHelper}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{MouseEvent, WebGLRenderingContext}
import org.scalajs.dom.{Element, PointerEvent, WheelEvent}

import scala.scalajs.js


object ViewerUi {
  def render(implicit state: ViewerState, update: ViewerState => Unit): VNode =
    tags.div(
      renderCanvas,
      renderControls
    )

  def renderCanvas(implicit state: ViewerState, update: ViewerState => Unit): VNode =
    tags.canvas(
      styles.build[String]("object-fit", "object-fit") := "cover",
      styles.position := "fixed",
      styles.left := "0",
      styles.top := "0",
      styles.width := "100vw",
      styles.height := "100vh",
      styles.zIndex := "-1",
      styles.overflow := "hidden",
      Hooks.insertHook { vnode => FractalRenderer.render(vnode.elm.get.asInstanceOf[Canvas], state.fractalProgram, true) },
      Hooks.postPatchHook { (_, newNode) => FractalRenderer.render(newNode.elm.get.asInstanceOf[Canvas], state.fractalProgram, true) },
      SnabbdomHelper.seq(canvasMouseEvents)
    )

  private def canvasMouseEvents(implicit state: ViewerState, update: ViewerState => Unit): Seq[Modifier[VNode, VNodeData]] = {
    val eventToPoint: PointerEvent => Point = event => (event.pageX, event.pageY)

    val startEvent =
      events.build[MouseEvent]("pointerdown") := { event =>
        update(state.copy(dragStartPosition = Some((event.pageX, event.pageY))))
      }

    def endEvent(startPosition: Point): PointerEvent => Unit = { event =>
      val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
      val translateA = state.fractalProgram.view.A * ((-event.pageX + startPosition._1) / boundingBox.width)
      val translateB = state.fractalProgram.view.B * ((event.pageY - startPosition._2) / boundingBox.height)
      val newView = state.fractalProgram.view.translate(translateA + translateB)
      event.target.asInstanceOf[js.Dynamic].style.left = "0px"
      event.target.asInstanceOf[js.Dynamic].style.top = "0px"
      update(state.copy(dragStartPosition = None, fractalProgram = state.fractalProgram.copy( view =newView)))
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
      val newView = state.fractalProgram.view
        .contain(boundingBox.width, boundingBox.height)
        .zoomSteps((x, y), if (steps > 0) -1 else 1)
      update(state.copy(fractalProgram = state.fractalProgram.copy(view = newView)))
    }

    startEvent +: wheelEvent +: inProgressEvents.getOrElse(Seq.empty)
  }

  def renderControls(implicit state: ViewerState, update: ViewerState => Unit): VNode =
    tags.div(
//      tags.button("reset", events.onClick := (() => update(state.copy(fractalProgram = state.fractalProgram.copy(view = Defaults.defaultViewport))))),
//      tags.button(s"more iterations (${state.fractalProgram.maxIterations})", events.onClick := (() => update(state.copy(maxIterations = state.maxIterations * 2)))),
//      tags.button(s"less iterations (${state.fractalProgram.maxIterations})", events.onClick := (() => update(state.copy(maxIterations = state.maxIterations / 2)))),
//      tags.button("toggle anit aliase", events.onClick := (() => update(state.copy(antiAliase = if (state.antiAliase == 2) 1 else 2)))),
//      tags.select(
//        tags.option("Mandelbrot"),
//        tags.option("JuliaSet"),
//        tags.option("Tricorn"),
//        tags.option("Newton"),
//        events.onChange := { event =>
//          val value = event.target.asInstanceOf[HTMLSelectElement].value
//          val newIteration = value match {
//            case "Mandelbrot" => MandelbrotIteration
//            case "JuliaSet" =>
//              val view = state.view
//              val center = view.origin + view.A * 0.5 + view.B * 0.5
//              JuliaSetIteration(Complex(center._1, center._2))
//            case "Tricorn" => TricornIteration
//            case "Newton" => NewtonIteration("x*x*x - 1")
//          }
//          update(state.copy(iteration = newIteration, shaded = newIteration.isInstanceOf[DeriveableIteration] && state.shaded))
//        }
//      ),
//      state.iteration match {
//        case NewtonIteration(function) =>
//          Some(tags.input(
//            attrs.`type` := "text",
//            attrs.value := function,
//            events.onChange := {event =>
//            val target = event.target.asInstanceOf[HTMLInputElement]
//            Parser.lang.parse(target.value) match {
//              case Some(_) =>
//                target.setCustomValidity("")
//                update(state.copy(iteration = NewtonIteration(target.value)))
//              case None =>
//                target.setCustomValidity("failed to parse")
//            }
//          }))
//        case _ => None
//      },
//      tags.button("toggle shaded", attrs.disabled := !state.iteration.isInstanceOf[DeriveableIteration], events.onClick := (() => update(state.copy(shaded = !state.shaded)))),
    )
}
