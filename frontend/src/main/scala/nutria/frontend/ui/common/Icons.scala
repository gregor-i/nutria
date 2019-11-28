package nutria.frontend.ui.common

import snabbdom.Snabbdom

object Icons {
  val upload = "upload"
  val save = "save"
  val edit = "edit"
  val info = "info-circle"
  val check = "check-circle"
  val cancel = "times-circle"
  val delete = "trash"
  val copy = "clone"
  val snapshot = "camera"

  def icon(name: String) =
    Snabbdom.h("span.icon")(
      Snabbdom.h(s"i.fa.fa-${name}")()
    )
}
