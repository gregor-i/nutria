package nutria.frontend.pages
package common

import snabbdom.Node

object ButtonList {
  def apply(buttons: Node*): Node = "div.buttons.is-right".child(buttons)
}
