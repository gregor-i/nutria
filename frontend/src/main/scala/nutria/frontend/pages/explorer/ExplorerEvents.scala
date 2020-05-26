package nutria.frontend.pages.explorer

import monocle.Lens
import nutria.core.{Point, Viewport}
import nutria.frontend.pages.ExplorerState
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import snabbdom.{Snabbdom, SnabbdomFacade}

import scala.scalajs.js

object ExplorerEvents {
  private def toSeq(touchList: TouchList): Seq[Touch] =
    Seq.tabulate(touchList.length)(touchList.apply)

  private def resetTransformCss(element: Canvas) =
    element.asInstanceOf[js.Dynamic].style.transform = ""

  private def transformCss(element: Canvas, moves: Seq[(Point, Point)]): Unit = {
    val (translate, scale, rotate) = Transform.transformations(moves)

    element.asInstanceOf[js.Dynamic].style.transform =
      s"translate(${translate._1 * 100}%, ${translate._2 * 100}%) " +
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

  def canvasWheelEvent[S](lens: Lens[S, Viewport])(implicit state: S, update: S => Unit): Seq[(String, SnabbdomFacade.Eventlistener)] = {
    val eventHandler =
      Snabbdom.specificEvent { event: PointerEvent =>
        event.preventDefault()
        val (_, boundingBox) = context(event)
        val p                = toPoint(event, boundingBox)
        val steps            = -event.asInstanceOf[WheelEvent].deltaY

        update(
          lens.modify {
            _.cover(boundingBox.width, boundingBox.height)
              .zoomSteps(p, steps / 50)
              .rotate(p, event.asInstanceOf[WheelEvent].deltaX / 200)
          }(state)
        )
      }
    Seq("wheel" -> eventHandler)
  }

  def canvasMouseEvents[S](lens: Lens[S, Viewport])(implicit state: S, update: S => Unit): Seq[(String, SnabbdomFacade.Eventlistener)] = {
    var startPosition = Option.empty[Point]

    val pointerDown =
      Snabbdom.specificEvent { event: PointerEvent =>
        if (event.pointerType == "mouse") {
          event.preventDefault()
          val (_, boundingBox) = context(event)
          startPosition = Some(toPoint(event, boundingBox))
        }
      }

    def pointerEnd =
      Snabbdom.specificEvent { event: PointerEvent =>
        if (event.pointerType == "mouse") {
          startPosition match {
            case Some(from) =>
              event.preventDefault()
              val (canvas, boundingBox) = context(event)
              val to                    = toPoint(event, boundingBox)
              val newView               = calcNewView(boundingBox, Seq(from -> to), lens.get(state))
              resetTransformCss(canvas)
              update(lens.set(newView)(state))
            case None => ()
          }
        }
      }

    def pointerMove =
      Snabbdom.specificEvent { event: PointerEvent =>
        if (event.pointerType == "mouse") {
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
      }

    //    fixme: use mousedown instead?
    Seq(
      "pointerdown"   -> pointerDown,
      "pointermove"   -> pointerMove,
      "pointerup"     -> pointerEnd,
      "pointercancel" -> pointerEnd,
      "pointerout"    -> pointerEnd
    )
  }

  def canvasTouchEvents[S](lens: Lens[S, Viewport])(
      implicit state: S,
      update: S => Unit
  ): Seq[(String, SnabbdomFacade.Eventlistener)] = {
    var moves = Map.empty[Double, TouchMove]

    val touchStart = Snabbdom.specificEvent { event: TouchEvent =>
      event.preventDefault()
      val (canvas, boundingBox) = context(event)

      val newTouches =
        toSeq(event.changedTouches)
          .map { touch =>
            val p = toPoint(touch, boundingBox)
            touch.identifier -> Processing(p, p)
          }
      moves ++= newTouches
    }

    val touchMove = Snabbdom.specificEvent { event: TouchEvent =>
      event.preventDefault()
      val (canvas, boundingBox) = context(event)
      val updates = for {
        t <- toSeq(event.changedTouches)
        start <- moves.get(t.identifier).collect {
          case Processing(start, _) => start
        }
      } yield t.identifier -> Processing(start, toPoint(t, boundingBox))

      moves ++= updates

      transformCss(canvas, moves.values.map(_.toMove).toSeq)
    }

    val touchEnd = Snabbdom.specificEvent { event: TouchEvent =>
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
          boundingBox = event.currentTarget.asInstanceOf[Element].getBoundingClientRect(),
          moves = moves.values.map(_.toMove).toSeq,
          view = lens.get(state)
        )

        resetTransformCss(canvas)
        update(lens.set(newView)(state))
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
