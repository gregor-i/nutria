package nutria.frontend.ui.common

import snabbdom.{Node, SnabbdomFacade, VNode}

object ButtonGroup {
  def apply(buttons: VNode*): Node =
    Node("div.field.has-addons")
      .child(buttons.map(button => Node("p").`class`("control").child(button).toVNode))
}

