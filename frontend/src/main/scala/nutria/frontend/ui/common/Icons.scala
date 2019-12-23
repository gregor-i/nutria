package nutria.frontend.ui.common

import snabbdom.{Node, VNode}

object Icons {
  val upload = "fa-upload"
  val save = "fa-save"
  val edit = "fa-edit"
  val info = "fa-info-circle"
  val check = "fa-check-circle"
  val cancel = "fa-times-circle"
  val delete = "fa-trash"
  val copy = "fa-clone" // fork
  val snapshot = "fa-camera"
  val explore = "fa-compass"
  val library = "fa-image"
  val login = "fa-sign-in"
  val logout = "fa-sign-out"
  val up = "fa-arrow-up"

  val publish = "fa-unlock"
  val unpublish = "fa-lock"

  val upvote = "fa-thumbs-o-up"
  val downvote = "fa-thumbs-o-down"

  def icon(icon: String): Node =
    Node("span.icon")
      .child(
        Node("i.fa").`class`(icon)
      )
}
