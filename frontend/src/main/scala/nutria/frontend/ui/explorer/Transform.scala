package nutria.frontend.ui.explorer

import nutria.core.viewport.Point._
import nutria.core.{Point, Viewport}

import scala.util.chaining._

object Transform {

  private final val identity = (1d, 0d, 0d, 1d, 0d, 0d)

  /**
    *
    * @param moves
    * @return 6 doubles describing a transformation matrix:
    *         | _1  _2  _3 |
    *         | _4  _5  _6 |
    *         |  0   0   1 |
    */
  def transformations(moves: Seq[(Point, Point)]): (Double, Double, Double, Double, Double, Double) =
    if (moves.isEmpty) {
      identity
    } else {
      val froms     = moves.map(_._1)
      val tos       = moves.map(_._2)
      val factor    = 1d / moves.length
      val fromAvg   = froms.reduce(_ + _) * factor
      val toAvg     = tos.reduce(_ + _) * factor
      val translate = toAvg - fromAvg
      if (moves.length == 1) {
        (1d, 0d, translate.x, 0d, 1d, translate.y)
      } else {
        val fromMinusAvg = froms.map(fromAvg - _)
        val toMinusAvg   = tos.map(toAvg - _)

        val fromScale = fromMinusAvg.map(_.abs).sum
        val toScale   = toMinusAvg.map(_.abs).sum
        val scale     = toScale / fromScale

        val rotation = moves.map {
          case (from, to) =>
            val u = to - toAvg
            val v = from - fromAvg
            Math.acos((u * v) / (u.abs * v.abs))
        }.sum * factor

        (scale * Math.cos(rotation), -Math.sin(rotation), translate.x, Math.sin(rotation), scale * Math.cos(rotation), translate.y)
      }
    }

  def transformsCss(moves: Seq[(Point, Point)]): String = {
    val matrix = transformations(moves)
    s"matrix(1, 0, 0, 1, 0, 0) translate(${matrix._3 * 100}%, ${matrix._6 * 100}%)"
  }

  def applyToViewport(moves: Seq[(Point, Point)], view: Viewport): Viewport = {
//    val (translate_, scale_, rotate_) = transformations(moves)
//
//    def translate(viewport: Viewport): Viewport =
//      viewport
//        .translate(viewport.A * translate_._1 + viewport.B * translate_._2)
//
//    def scale(viewport: Viewport): Viewport =
//      viewport.zoom((0.5, 0.5), scale_)
//
//    def rotate(viewport: Viewport): Viewport =
//      viewport.rotate((0.5, 0.5), rotate_)
//
//    view
//      .pipe(translate)
//      .pipe(scale)
//      .pipe(rotate)
    view
  }
}
