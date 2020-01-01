package nutria.frontend.ui.common

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
    Node("button.button")
      .event("click", onclick)
      .child(Icons.icon(icon))
      .style("border-radius", "50%")
}
