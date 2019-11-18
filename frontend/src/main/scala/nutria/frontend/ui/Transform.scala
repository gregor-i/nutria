package nutria.frontend.ui

import nutria.core.viewport.Point._
import nutria.core.{Point, Viewport}

object Transform {
  // translation, scale, rotation
  // todo: implement rotation
  def transformations(moves: Seq[(Point, Point)]): (Point, Double, Double) =
    if (moves.isEmpty) {
      ((0d, 0d), 1d, 0d)
    } else {
      val factor = (1d / moves.length)
      val fromCenter = moves.map(_._1).reduce(_ + _) * factor
      val toCenter = moves.map(_._2).reduce(_ + _) * factor
      val translate = toCenter - fromCenter
      if (moves.length == 1) {
        (translate, 1d, 0d)
      } else {
        val fromScale = moves.map(p => (p._1 - fromCenter).norm()).sum * factor
        val toScale = moves.map(p => (p._2 - toCenter).norm()).sum * factor
        val scale = toScale / fromScale
        (translate, scale, 0d)
      }
    }

  def applyToViewport(moves: Seq[(Point, Point)], view: Viewport): Viewport = {
    val (translate_, scale_, rotate_) = transformations(moves.map(_.swap))

    def translate(viewport: Viewport): Viewport =
      viewport
        .translate(viewport.A * translate_._1 + viewport.B * translate_._2)

    def scale(viewport: Viewport): Viewport =
      view.zoom((0.5, 0.5), scale_)

    translate(scale(view))
  }
}
