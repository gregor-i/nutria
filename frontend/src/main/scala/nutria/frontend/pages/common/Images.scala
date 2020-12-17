package nutria.frontend.pages
package common

import snabbdom.Node

object Images {
  val icon = "/assets/icon.png"

  val compileError = "/assets/compile_error.svg"
  val rendering    = "/assets/rendering.svg"

  def apply(src: String): Node = "img".attr("src", src)
}
