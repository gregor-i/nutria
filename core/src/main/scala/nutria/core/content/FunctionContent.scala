package nutria.core.content

import nutria.core.{Point, Transform}

case class FunctionContent[B](function: Point => B, transform: Transform) extends Content[B]{
  val dimensions = transform.dimensions
  def apply(x:Int, y:Int): B = function(transform(x, y))
}
