package nutria.frontend.pages.common

import snabbdom.Node

object Images {
  val icon = "/assets/icon.png"

  val compileError = "/assets/compile_error.svg"
  val rendering    = "/assets/rendering.svg"

  def apply(src: String): Node = Node("img").attr("src", src)
}
