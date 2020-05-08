package nutria.frontend.pages.common

import snabbdom.Node

object Images {
  val icon = "/assets/icon.png"

  val exampleDivergingSeries = "/assets/example_DivergingSeries.png"
  val exampleNewtonIteration = "/assets/example_NewtonIteration.png"

  def apply(src: String): Node = Node("img").attr("src", src)
}
