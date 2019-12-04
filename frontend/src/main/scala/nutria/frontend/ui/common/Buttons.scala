package nutria.frontend.ui.common

import snabbdom.{Builder, SnabbdomFacade, VNode}

object Buttons {
  def apply(text: String, icon: String,
            onclick: SnabbdomFacade.Eventlistener,
            `class`: String = "",
            disabled: Boolean = false) =
    Builder("button")
      .`class`(`class`)
      .event("click", onclick)
      .attr("disabled", disabled.toString)
      .child(Icons.icon(icon))
      .child(Builder("span").child(text))
      .toVNode

  def group(buttons: VNode*): VNode =
    Builder("div.field.has-addons")
      .child(buttons.map(button => Builder("p").`class`("control").child(button).toVNode))
      .toVNode
}
