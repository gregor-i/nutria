package nutria.frontend.pages
package common

import snabbdom.{Eventlistener, Node}

object Button {
  def apply(text: String, icon: String, onclick: Eventlistener): Node =
    "button.button"
      .event("click", onclick)
      .child(Icons.icon(icon))
      .child("span".text(text))

  def apply(text: String, onclick: Eventlistener): Node =
    "button.button"
      .event("click", onclick)
      .text(text)

  def icon(icon: String, onclick: Eventlistener, round: Boolean = true): Node =
    "button.button"
      .`class`("is-rounded", round)
      .event("click", onclick)
      .child(Icons.icon(icon))

}
