package nutria.frontend.common

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple._
import org.scalajs.dom

object Header {
  def apply(name: String): VNode =
    tags.div(
      attrs.className := "top-bar",
      tags.div(
        tags.img(
          styles.height := "100%",
          attrs.src := "/img/icon.png",
          events.onClick := (() => dom.window.location.assign("/")),
          styles.cursor := "pointer"
        ),
        tags.span(name)
      )
    )
}
