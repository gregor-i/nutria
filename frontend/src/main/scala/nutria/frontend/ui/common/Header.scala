package nutria.frontend.ui.common

import nutria.core.User
import nutria.frontend.{LoadingState, NutriaState}
import snabbdom.{Node, Snabbdom, VNode}

object Header {

  def apply(implicit state: NutriaState, update: NutriaState => Unit): VNode = {
    Node("nav.navbar.is-light")
      .attr("role", "navigation")
      .attr("aria-label", "main navigation")
      .child(
        Node("div.navbar-brand")
          .child(brand)
          .child(burgerMenu)
      )
      .child(
        Node("div.navbar-menu")
          .`class`("is-active", state.navbarExpanded)
          .child(
            Node("div.navbar-start")
              .child(Node("a.navbar-item")
                .text("Library")
                .event("click", Snabbdom.event(_ => update(LoadingState(NutriaState.libraryState()))))
              )
            //.child(Node("a.navbar-item").text("Profile"))
          )
          .child(
            Node("div.navbar-end")
              .child(
                state.user match {
                  case Some(user) => logoutItem(user)
                  case None => loginItem
                }
              )
          )
      )
      .toVNode
  }

  private val brand =
    Node("div.navbar-item")
      .child(
        Node("img")
          .attr("src", "/img/icon.png")
          .style("max-height", "36px !important")
      )
      .child(
        Node("span")
          .style("font-size", "28px")
          .style("line-height", "28px")
          .style("color", "black")
          .style("margin-left", "4px")
          .text("Nutria")
      )

  private def burgerMenu(implicit state: NutriaState, update: NutriaState => Unit) =
    Node("a.navbar-burger.burger")
      .event("click", Snabbdom.event(_ => update(NutriaState.setNavbarExtended(state, !state.navbarExpanded))))
      .`class`("is-active", state.navbarExpanded)
      .attr("aria-label", "menu")
      .attr("aria-expanded", "false")
      .child(Node("span").attr("aria-hidden", "true"))
      .child(Node("span").attr("aria-hidden", "true"))
      .child(Node("span").attr("aria-hidden", "true"))

  private val loginItem =
    Node("div.navbar-item")
      .child(
        Node("a.button")
          .attr("href", "/auth/google")
          .child(Icons.icon(Icons.login))
          .child(Node("span").text("Log in"))
      )

  private def logoutItem(user: User) =
    Node("div.navbar-item")
      .child(
        Node("a.button.is-outlined")
          .attr("href", "/auth/logout")
          .child(Icons.icon(Icons.logout))
          .child(Node("span").text(s"Log out")),
      )
}
