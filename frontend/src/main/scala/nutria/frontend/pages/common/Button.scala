package nutria.frontend.pages.common

import snabbdom.{Node, SnabbdomFacade}

object Button {
  def apply(text: String, icon: String, onclick: SnabbdomFacade.Eventlistener): Node =
    Node("button.button")
      .event("click", onclick)
      .child(Icons.icon(icon))
      .child(Node("span").text(text))

  def apply(text: String, onclick: SnabbdomFacade.Eventlistener): Node =
    Node("button.button")
      .event("click", onclick)
      .text(text)

  def icon(icon: String, onclick: SnabbdomFacade.Eventlistener): Node =
    Node("button.button.is-rounded")
      .event("click", onclick)
      .child(Icons.icon(icon))

  def list(): Node =
    Node("div.buttons.is-right")
}
