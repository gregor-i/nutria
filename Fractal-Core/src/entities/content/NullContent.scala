package entities.content

import entities.Dimensions

case class NullContent(dimensions:Dimensions) extends Content with Normalized {
  override def apply(x: Int, y: Int): Double = 0
}
