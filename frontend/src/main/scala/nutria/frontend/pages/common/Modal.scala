package nutria.frontend.pages.common

import snabbdom.{Event, Node}

import scala.scalajs.js

object Modal {
  def apply(closeAction: js.Function1[Event, Unit])(content: Node*): Node =
    Node("div.modal.is-active")
      .child(Node("div.modal-background").event("click", closeAction))
      .child(Node("div.modal-content").child(Node("div.box").child(content)))
}
