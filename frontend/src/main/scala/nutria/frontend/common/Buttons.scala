package nutria.frontend.common

import snabbdom.Snabbdom.h
import snabbdom.{SnabbdomNative, VNode}

object Buttons {
  def apply(text: String, imgSrc: String,
                    onclick: SnabbdomNative.Eventlistener,
                    `class`: String = "",
                    disabled: Boolean = false) =
    h("button.button" + `class`,
      events = Seq("click" -> onclick),
      attrs = if (disabled) Seq("disabled" -> "true") else Seq.empty
    )(
      h("span.icon")(
        h("img", attrs = Seq("src" -> imgSrc))()
      ),
      h("span")(text)
    )

  def group(buttons: VNode*): VNode =
    h("div.field.has-addons")(
      buttons.map(h("p.control")(_)): _*
    )
}
