package nutria.frontend.common

import nutria.frontend.NutriaState
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}
import org.scalajs.dom

object Header {
  def apply(name: String)(implicit state: NutriaState, update: NutriaState => Unit): VNode =
    h("div.top-bar")(
      h("div")(
        h("img",
          styles = Seq(
            "height" -> "100%",
            "cursor" -> "pointer",
          ),
          attrs = Seq("src" -> "/img/icon.png"),
          events = Seq("click" -> Snabbdom.event(_ => dom.window.location.replace("/"))),
        )(),
        h("span")(name)
      )
    )
}
