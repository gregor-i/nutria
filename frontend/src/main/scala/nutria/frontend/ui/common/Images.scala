package nutria.frontend.ui.common

import snabbdom.Node

object Images {
  val icon = "/img/icon.png"

  val exampleDivergingSeries = "/img/example_DivergingSeries.png"
  val exampleNewtonIteration = "/img/example_NewtonIteration.png"

  def apply(src: String): Node =
    Node("img")
      .attr("src", src)
}
