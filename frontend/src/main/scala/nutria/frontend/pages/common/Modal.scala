package nutria.frontend.pages.common

import snabbdom.Node
import snabbdom.SnabbdomFacade.Eventlistener

object Modal {
  def apply(closeAction: Eventlistener)(content: Node*): Node =
    Node("div.modal.is-active")
      .child(Node("div.modal-background").event("click", closeAction))
      .child(Node("div.modal-content").child(Node("div.box").child(content)))
}
