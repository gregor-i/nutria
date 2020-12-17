package nutria.frontend.pages.common

import snabbdom.{Eventlistener, Node}

object Button {
  def apply(text: String, icon: String, onclick: Eventlistener): Node =
    Node("button.button")
      .event("click", onclick)
      .child(Icons.icon(icon))
      .child(Node("span").text(text))

  def apply(text: String, onclick: Eventlistener): Node =
    Node("button.button")
      .event("click", onclick)
      .text(text)

  def icon(icon: String, onclick: Eventlistener, round: Boolean = true): Node =
    Node("button.button")
      .`class`("is-rounded", round)
      .event("click", onclick)
      .child(Icons.icon(icon))

}
