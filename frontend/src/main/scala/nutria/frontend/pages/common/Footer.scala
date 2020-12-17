package nutria.frontend.pages
package common

import nutria.macros.StaticContent
import snabbdom.Node

object Footer {
  def apply() =
    "footer.footer"
      .prop("innerHTML", StaticContent("frontend/src/main/html/footer.html"))
}
