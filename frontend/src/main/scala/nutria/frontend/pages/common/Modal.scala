package nutria.frontend.pages
package common

import snabbdom.{Event, Node}

import scala.scalajs.js

object Modal {
  def apply(closeAction: js.Function1[Event, Unit])(content: Node*): Node =
    "div.modal.is-active"
      .child("div.modal-background".event("click", closeAction))
      .child("div.modal-content".child("div.box".child(content)))
}
