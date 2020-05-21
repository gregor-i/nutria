package nutria.frontend.pages.common

import snabbdom.Node

object ButtonList {
  def apply(buttons: Node*): Node = Node("div.buttons.is-right").child(buttons)
}
