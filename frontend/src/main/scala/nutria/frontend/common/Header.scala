package nutria.frontend.common

import nutria.frontend.{ExplorerState, LibraryState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

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
          events = Seq("click" -> Snabbdom.event(_ => update(state match {
            case explorerState: ExplorerState => LibraryState(explorerState.fractals)
            case libState: LibraryState => libState
          }))),
        )(),
        h("span")(name)
      )
    )
}
