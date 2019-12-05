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
  val explore = "fa-compass"
  val library = "fa-image"
  val login = "fa-sign-in"
  val logout = "fa-sign-out"

  def icon(icon: String): VNode =
    Builder("span.icon")
      .child(
        Builder("i.fa").`class`(icon)
      )
      .toVNode
}
