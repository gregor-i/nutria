package nutria.frontend.ui.common

import snabbdom.{Node, SnabbdomFacade, VNode}

object ButtonGroup {
  def apply(buttons: VNode*): Node =
    Node("div.buttons")
      .children(buttons)
}

