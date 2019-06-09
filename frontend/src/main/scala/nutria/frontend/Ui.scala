package nutria.frontend

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple._
import nutria.frontend.util.Hooks
import org.scalajs.dom.Element
import org.scalajs.dom.html.Canvas
import nutria.core.viewport.Point._
import nutria.data.Defaults
import nutria.frontend.shaderBuilder.{DeriveableIteration, JuliaSetIteration, MandelbrotIteration, TricornIteration}
import org.scalajs.dom.raw.{ClientRect, HTMLSelectElement, MouseEvent, WebGLRenderingContext, WheelEvent}
import spire.math.Complex


object Ui {
  def render(implicit state: State, update: State => Unit): VNode =
    tags.div(
      renderCanvas,
      renderControls
    )

  def renderCanvas(implicit state: State, update: State => Unit): VNode =
    tags.div(
      styles.position := "fixed",
      styles.left := "0",
      styles.top := "0",
      styles.right := "0",
      styles.bottom := "0",
      styles.zIndex := "-1",
      styles.overflow := "hidden",
      tags.canvas(
        styles.build[String]("object-fit", "object-fit") := "cover",
        styles.width := "100%",
        styles.height := "100%",
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
        events.onClick := { event =>
          val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
          val x = (event.clientX - boundingBox.left) / boundingBox.width
          val y = 1 - (event.clientY - boundingBox.top) / boundingBox.height
          val newView = state.view
            .contain(boundingBox.width, boundingBox.height)
            .focus(x, y)
          update(state.copy(view = newView))
        },
        events.onWheel := { event =>
          val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
          val x = (event.clientX - boundingBox.left) / boundingBox.width
          val y = 1 - (event.clientY - boundingBox.top) / boundingBox.height
          val steps = event.asInstanceOf[WheelEvent].deltaY
          val newView = state.view
            .contain(boundingBox.width, boundingBox.height)
            .zoomSteps((x, y), if (steps > 0) -1 else 1)
          update(state.copy(view = newView))
        }
      )
    )

  def renderControls(implicit state: State, update: State => Unit): VNode =
    tags.div(
      tags.button("reset", events.onClick := (() => update(state.copy(view = Defaults.defaultViewport)))),
      tags.button("zoom in", events.onClick := (() => update(state.copy(view = state.view.zoomIn())))),
      tags.button("zoom out", events.onClick := (() => update(state.copy(view = state.view.zoomOut())))),
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
