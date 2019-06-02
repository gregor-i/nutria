package nutria.frontend

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple._
import nutria.core.content.LinearNormalized
import nutria.frontend.util.Hooks
import org.scalajs.dom.{CanvasRenderingContext2D, Element}
import org.scalajs.dom.html.Canvas
import nutria.core.syntax._
import nutria.data.Defaults
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.sequences.Mandelbrot
import org.scalajs.dom.raw.{MouseEvent, WheelEvent}


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
        attrs.build[String]("width") := (state.dim.width + "px"),
        attrs.build[String]("height") := (state.dim.height + "px"),
        Hooks.insertHook { vnode =>
          val canvas = vnode.elm.get.asInstanceOf[Canvas]
          val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
          draw(ctx)
        },
        Hooks.postPatchHook { (_, newNode) =>
          val canvas = newNode.elm.get.asInstanceOf[Canvas]
          val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
          draw(ctx)
        },
        events.onClick := { event =>
          val (x, y) = relPosition(event)
          val newView = state.view.focus(x, y)
          update(state.copy(view = newView))
        },
        events.onWheel := { event =>
          val (x, y) = relPosition(event)
          val steps = event.asInstanceOf[WheelEvent].deltaY
          val newView = state.view.zoomSteps((x, y), -(steps / 3).round.toInt)
          update(state.copy(view = newView))
        }
      )
    )

  def relPosition(event: MouseEvent): (Double, Double) = {
    val boundingBox = event.target.asInstanceOf[Element].getBoundingClientRect()
    val relX = (event.clientX -boundingBox.left) / boundingBox.width
    val relY = (event.clientY -boundingBox.top) / boundingBox.height
    (relX, relY)
  }

  def renderControls(implicit state: State, update: State => Unit): VNode =
    tags.div(
      tags.button("reset", events.onClick := (() => update(state.copy(view = Defaults.defaultViewport)))),
      tags.button("zoom in", events.onClick := (() => update(state.copy(view = state.view.zoomIn())))),
      tags.button("zoom out", events.onClick := (() => update(state.copy(view = state.view.zoomOut())))),
      tags.button("right", events.onClick := (() => update(state.copy(view = state.view.right())))),
      tags.button("up", events.onClick := (() => update(state.copy(view = state.view.up())))),
      tags.button("left", events.onClick := (() => update(state.copy(view = state.view.left())))),
      tags.button("down", events.onClick := (() => update(state.copy(view = state.view.down())))),
      tags.button("cover", events.onClick := (() => update(state.copy(view = state.view.cover(4, 3))))),
      tags.button("contain", events.onClick := (() => update(state.copy(view = state.view.contain(4, 3))))),
    )

  def draw(ctx: CanvasRenderingContext2D)(implicit state: State): Unit = {
    val fractal = Mandelbrot.apply(50) andThen
      CountIterations.smoothed() andThen
      LinearNormalized.apply(0, 50) andThen
      Wikipedia

    val img = state.view
      .withDimensions(state.dim)
      .withContent(fractal)

    for {
      x <- 0 until state.dim.width
      y <- 0 until state.dim.height
    } {
      ctx.fillStyle = img(x, y).toString
      ctx.fillRect(x, y, 1, 1)
    }
  }

}
