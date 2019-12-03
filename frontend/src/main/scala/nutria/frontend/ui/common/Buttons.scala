package nutria.frontend.ui.common

import snabbdom.Snabbdom.h
import snabbdom.{Builder, SnabbdomFacade, VNode}

object Buttons {
  def apply(text: String, icon: String,
            onclick: SnabbdomFacade.Eventlistener,
            `class`: String = "",
            disabled: Boolean = false) =
    Builder("button")
      .classes(`class`)
      .event("click", onclick)
      .attr("disabled", disabled.toString)
      .child(Icons.icon(icon))
      .child(Builder.span.child(text))
      .toVNode

  def group(buttons: VNode*): VNode =
    h("div.field.has-addons")(
      buttons.map(h("p.control")(_)): _*
    )
}
