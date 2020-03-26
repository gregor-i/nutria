package nutria.frontend.ui.common

import nutria.core.User
import nutria.frontend._
import snabbdom.{Node, Snabbdom}

object Header {

  def apply()(implicit state: NutriaState, update: NutriaState => Unit): Node = {
    Node("nav.navbar.is-light.has-shadow")
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
              .child(
                Link
                  .async("/gallery", Links.galleryState())
                  .classes("navbar-item")
                  .text("Public Gallery")
              )
              .child(
                Link(FAQState(state.user))
                  .classes("navbar-item")
                  .text("FAQ")
              )
              .childOptional(
                state.user.map(
                  user =>
                    Link
                      .async(s"/user/${user.id}/gallery", Links.userGalleryState(user.id))
                      .classes("navbar-item")
                      .text("My Gallery")
                )
              )
              .childOptional(
                state.user.map(
                  user =>
                    Link(ProfileState(user))
                      .classes("navbar-item")
                      .text("My Profile")
                )
              )
          )
          .child(
            Node("div.navbar-end")
              .child(
                state.user match {
                  case Some(user) => logoutItem(user)
                  case None       => loginItem
                }
              )
          )
      )
  }

  def loginHref(implicit nutriaState: NutriaState): String = {
    Router.stateToUrl(nutriaState) match {
      case None                 => "/auth/google"
      case Some((base, search)) => s"/auth/google?return-to=${base + Router.searchToUrl(search)}"
    }
  }

  val logoutHref: String = "/auth/logout"

  private val brand =
    Node("div.navbar-item")
      .child(
        Node("img")
          .attr("src", "/img/icon.png")
          .style("height", "36px")
          .style("width", "36px")
          .style("max-height", "36px")
      )
      .child(
        Node("span")
          .style("font-size", "36px")
          .style("line-height", "36px")
          .style("color", "black")
          .style("margin-left", "4px")
          .text("Nutria")
      )

  private def burgerMenu(implicit state: NutriaState, update: NutriaState => Unit) =
    Node("a.navbar-burger.burger")
      .event(
        "click",
        Snabbdom.event(_ => update(NutriaState.setNavbarExtended(state, !state.navbarExpanded)))
      )
      .`class`("is-active", state.navbarExpanded)
      .attr("aria-label", "menu")
      .attr("aria-expanded", "false")
      .child(Node("span").attr("aria-hidden", "true"))
      .child(Node("span").attr("aria-hidden", "true"))
      .child(Node("span").attr("aria-hidden", "true"))

  private def loginItem(implicit nutriaState: NutriaState) =
    Node("div.navbar-item")
      .child(
        Node("a.button.is-rounded")
          .attr("href", loginHref)
          .child(Icons.icon(Icons.login))
          .child(Node("span").text("Log in"))
      )

  private def logoutItem(user: User) =
    Node("div.navbar-item")
      .child(
        Node("a.button.is-rounded")
          .attr("href", logoutHref)
          .child(Icons.icon(Icons.logout))
          .child(Node("span").text(s"Log out"))
      )
}
