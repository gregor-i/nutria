package nutria.frontend.common

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.frontend.{ExplorerState, LibraryState, NutriaState}

object Header {
  def apply(name: String)(implicit state: NutriaState, update: NutriaState => Unit): VNode =
    tags.div(
      attrs.className := "top-bar",
      tags.div(
        tags.img(
          styles.height := "100%",
          attrs.src := "/img/icon.png",
          events.onClick := (() => update(state match {
            case explorerState: ExplorerState => LibraryState(explorerState.fractals)
            case libState: LibraryState => libState
          })),
          styles.cursor := "pointer"
        ),
        tags.span(name)
      )
    )
}
