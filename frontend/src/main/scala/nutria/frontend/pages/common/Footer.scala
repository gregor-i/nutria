package nutria.frontend.pages.common

import nutria.macros.StaticContent
import snabbdom.Node

object Footer {
  def apply() =
    Node("footer.footer")
      .prop("innerHTML", StaticContent("frontend/src/main/html/footer.html"))
}
