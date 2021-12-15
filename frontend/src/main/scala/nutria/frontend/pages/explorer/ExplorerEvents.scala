package nutria.frontend.pages.explorer

import nutria.core.{Point, Viewport}
import nutria.frontend.util.Updatable
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import snabbdom.Eventlistener

import scala.scalajs.js

object ExplorerEvents {
  private def toSeq(touchList: TouchList): Seq[Touch] =
    Seq.tabulate(touchList.length)(touchList.apply)

  private def resetTransformCss(element: Canvas) = {
    // The reset will be performed by the canvas hooks after rendering
    // element.asInstanceOf[js.Dynamic].style.transform = ""
  }

  private def transformCss(element: Canvas, moves: Seq[(Point, Point)]): Unit = {
    val (translate, scale, rotate) = Transform.transformations(moves)

    element.asInstanceOf[js.Dynamic].style.transform = s"translate(${translate._1 * 100}%, ${translate._2 * 100}%) " +
      s"scale($scale)" +
      s"rotate(${rotate * 180d / Math.PI}deg)"
  }

  private def toPoint(event: MouseEvent, boundingBox: ClientRect): Point = {
    val x = (event.clientX - boundingBox.left) / boundingBox.width
    val y = (event.clientY - boundingBox.top) / boundingBox.height
    (x, y)
  }

  private def toPoint(touch: Touch, boundingBox: ClientRect): Point = {
    val x = (touch.clientX - boundingBox.left) / boundingBox.width
    val y = (touch.clientY - boundingBox.top) / boundingBox.height
    (x, y)
  }

  private def context(event: UIEvent): (Canvas, ClientRect) = {
    val container = event.currentTarget.asInstanceOf[html.Div]
    (container.firstElementChild.asInstanceOf[Canvas], container.getBoundingClientRect())
  }

  private def calcNewView(boundingBox: ClientRect, moves: Seq[(Point, Point)], view: Viewport) =
    Transform.applyToViewport(moves, view.cover(boundingBox.width, boundingBox.height))

  def canvasWheelEvent(updatable: Updatable[Viewport, Viewport]): Seq[(String, Eventlistener)] = {
    val eventHandler = { event: MouseEvent =>
      event.preventDefault()
      val (_, boundingBox) = context(event)
      val p                = toPoint(event, boundingBox)
      val steps            = -event.asInstanceOf[WheelEvent].deltaY

      updatable.update(
        updatable.state
          .cover(boundingBox.width, boundingBox.height)
          .zoomSteps(p, steps / 50.0)
          .rotate(p, event.asInstanceOf[WheelEvent].deltaX / 200.0)
      )
    }
    Seq("wheel" -> eventHandler)
  }

  def canvasMouseEvents(updatable: Updatable[Viewport, Viewport]): Seq[(String, Eventlistener)] = {
    var startPosition = Option.empty[Point]

    val pointerDown = { event: MouseEvent =>
      event.preventDefault()
      val (_, boundingBox) = context(event)
      startPosition = Some(toPoint(event, boundingBox))
    }

    def pointerEnd = { event: MouseEvent =>
      startPosition match {
        case Some(from) =>
          event.preventDefault()
          val (canvas, boundingBox) = context(event)
          val to                    = toPoint(event, boundingBox)
          val newView               = calcNewView(boundingBox, Seq(from -> to), updatable.state)
          resetTransformCss(canvas)
          updatable.update(newView)
        case None => ()
      }
    }

    def pointerMove = { event: MouseEvent =>
      startPosition match {
        case Some(from) =>
          event.preventDefault()
          val (canvas, boundingBox) = context(event)
          val to                    = toPoint(event, boundingBox)
          transformCss(
            element = canvas,
            moves = Seq(from -> to)
          )
        case None => ()
      }
    }

    Seq(
      "mousedown"   -> pointerDown,
      "mousemove"   -> pointerMove,
      "mouseup"     -> pointerEnd,
      "mousecancel" -> pointerEnd,
      "mouseout"    -> pointerEnd
    )
  }

  def canvasTouchEvents(updatable: Updatable[Viewport, Viewport]): Seq[(String, Eventlistener)] = {
    var moves = Map.empty[Double, TouchMove]

    val touchStart = { event: TouchEvent =>
      event.preventDefault()
      val (_, boundingBox) = context(event)

      val newTouches =
        toSeq(event.changedTouches)
          .map { touch =>
            val p = toPoint(touch, boundingBox)
            touch.identifier -> Processing(p, p)
          }
      moves ++= newTouches
    }

    val touchMove = { event: TouchEvent =>
      event.preventDefault()
      val (canvas, boundingBox) = context(event)
      val updates = for {
        t <- toSeq(event.changedTouches)
        start <- moves.get(t.identifier).collect { case Processing(start, _) =>
          start
        }
      } yield t.identifier -> Processing(start, toPoint(t, boundingBox))

      moves ++= updates

      transformCss(canvas, moves.values.map(_.toMove).toSeq)
    }

    val touchEnd = { event: TouchEvent =>
      event.preventDefault()
      val (canvas, boundingBox) = context(event)

      val updated = for {
        t     <- toSeq(event.changedTouches)
        state <- moves.get(t.identifier)
      } yield state match {
        case Ended(start, _)      => t.identifier -> Ended(start, toPoint(t, boundingBox))
        case Processing(start, _) => t.identifier -> Ended(start, toPoint(t, boundingBox))
      }

      moves ++= updated

      if (moves.values.forall(_.isInstanceOf[Ended])) {
        val newView = calcNewView(
          boundingBox = boundingBox,
          moves = moves.values.map(_.toMove).toSeq,
          view = updatable.state
        )

        resetTransformCss(canvas)
        updatable.update(newView)
      }
    }

    Seq(
      "touchstart" -> touchStart,
      "touchmove"  -> touchMove,
      "touchend"   -> touchEnd
    )
  }
}

private sealed trait TouchMove {
  def toMove: (Point, Point) = this match {
    case Processing(from, to) => from -> to
    case Ended(from, to)      => from -> to
  }
}

private case class Processing(start: Point, currently: Point) extends TouchMove

private case class Ended(start: Point, end: Point) extends TouchMove
