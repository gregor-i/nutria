package nutria.frontend.pages.common

import snabbdom.Node

object Icons {
  val upload    = "fa-upload"
  val save      = "fa-save"
  val edit      = "fa-edit"
  val info      = "fa-info-circle"
  val check     = "fa-check-circle"
  val cancel    = "fa-times-circle"
  val delete    = "fa-trash"
  val copy      = "fa-code-branch"
  val snapshot  = "fa-camera"
  val explore   = "fa-compass"
  val gallery   = "fa-image"
  val login     = "fa-sign-in-alt"
  val logout    = "fa-sign-out-alt"
  val up        = "fa-arrow-up"
  val download  = "fa-download"
  val publish   = "fa-unlock-alt"
  val unpublish = "fa-unlock-alt"
  val upvote    = "fa-thumbs-up"
  val downvote  = "fa-thumbs-down"
  val plus      = "fa-plus"

  def icon(icon: String): Node =
    Node("span.icon")
      .child(
        Node("i.fas").`class`(icon)
      )
}
