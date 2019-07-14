package nutria.frontend.common

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple.{VNode, attrs, styles, tags}

object Header {
  def apply(name: String): VNode =
    tags.div(
      attrs.className := "top-bar",
      tags.div(
        tags.img(
          styles.height := "100%",
          attrs.src := "/img/icon.png",
        ),
        tags.span(name)
      )
    )
}
