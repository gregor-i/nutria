package nutria.frontend.ui.common

import snabbdom.{Builder, VNode}

object Icons {
  val upload = "fa-upload"
  val save = "fa-save"
  val edit = "fa-edit"
  val info = "fa-info-circle"
  val check = "fa-check-circle"
  val cancel = "fa-times-circle"
  val delete = "fa-trash"
  val copy = "fa-clone"
  val snapshot = "fa-camera"

  def icon(icon: String): VNode =
    Builder.span.classes("icon")
      .child(
        Builder.i.classes("fa", icon)
      )
      .toVNode
}
