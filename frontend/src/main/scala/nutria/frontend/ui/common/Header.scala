package nutria.frontend.ui.common

import nutria.core.User
import nutria.frontend.toasts.Toasts
import nutria.frontend.{LoadingState, NutriaState, ProfileState}
import org.scalajs.dom
import snabbdom.{Node, Snabbdom}

object Header {

  def apply(implicit state: NutriaState, update: NutriaState => Unit): Node = {
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
                Node("a.navbar-item")
                  .text("Public Gallery")
                  .event(
                    "click",
                    Snabbdom.event(_ => update(LoadingState(NutriaState.libraryState())))
                  )
              )
              .child(
                Node("a.navbar-item")
                  .text("My Gallery")
                  .event(
                    "click",
                    Snabbdom.event { _ =>
                      state.user match {
                        case Some(user) =>
                          update(LoadingState(NutriaState.userLibraryState(user.id)))
                        case None => Toasts.dangerToast("Log in first")
                      }
                    }
                  )
              )
              .child(
                Node("a.navbar-item")
                  .text("My Profile")
                  .event(
                    "click",
                    Snabbdom.event { _ =>
                      state.user match {
                        case Some(user) =>
                          update(ProfileState(user))
                        case None => Toasts.dangerToast("Log in first")
                      }
                    }
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

  val loginHref: String  = "/auth/google"
  val logoutHref: String = "/auth/logout"

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

  private val loginItem =
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
