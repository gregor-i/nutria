package nutria.frontend.pages.common

import monocle.Lens
import nutria.api.User
import nutria.frontend._
import nutria.frontend.pages.{AdminState, DocumentationState, ProfileState, TemplateGalleryState}
import nutria.frontend.util.SnabbdomUtil
import snabbdom.Node

object Header {

  def apply[S <: PageState](lens: Lens[S, Boolean])(implicit globalState: GlobalState, state: S, update: PageState => Unit): Node = {
    Node("nav.navbar")
      .attr("role", "navigation")
      .attr("aria-label", "main navigation")
      .child(
        Node("div.navbar-brand")
          .child(brand)
          .child(burgerMenu(lens))
      )
      .child(
        Node("div.navbar-menu")
          .`class`("is-active", lens.get(state))
          .child(
            Node("div.navbar-start")
              .child(
                Link
                  .async("/gallery", Links.galleryState())
                  .classes("navbar-item")
                  .text("Public Gallery")
              )
              .child(
                Link(DocumentationState.introduction)
                  .classes("navbar-item")
                  .text("Introduction")
              )
              .child(
                Link(DocumentationState.faq)
                  .classes("navbar-item")
                  .text("FAQ")
              )
              .childOptional(
                globalState.user.map(
                  user =>
                    Link
                      .async(s"/user/${user.id}/gallery", Links.userGalleryState(user.id))
                      .classes("navbar-item")
                      .text("My Gallery")
                )
              )
              .childOptional(
                globalState.user.map(
                  _ =>
                    Link
                      .async("/templates", TemplateGalleryState.load(globalState))
                      .classes("navbar-item")
                      .text("My Templates")
                )
              )
              .childOptional(
                globalState.user.map(
                  user =>
                    Link(ProfileState(user))
                      .classes("navbar-item")
                      .text("My Profile")
                )
              )
              .childOptional(
                Some(
                  Link
                    .async("/admin", AdminState.initial())
                    .classes("navbar-item")
                    .text("Admin")
                ).filter(_ => globalState.user.exists(_.admin))
              )
          )
          .child(
            Node("div.navbar-end")
              .child(
                globalState.user match {
                  case Some(user) => logoutItem(user)
                  case None       => loginItem
                }
              )
          )
      )
  }

  def fab(node: Node): Node =
    node
      .classes("button", "is-large", "is-primary", "is-rounded", "is-fab")

  def loginHref(implicit nutriaState: PageState): String = {
    Router.stateToUrl(nutriaState) match {
      case None                 => "/auth/google"
      case Some((base, search)) => s"/auth/google?return-to=${base + Router.queryParamsToUrl(search)}"
    }
  }

  val logoutHref: String = "/auth/logout"

  private val brand =
    Node("div.navbar-item")
      .child(
        Images(Images.icon)
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

  private def burgerMenu[S <: PageState](lens: Lens[S, Boolean])(implicit globalState: GlobalState, state: S, update: PageState => Unit) =
    Node("a.navbar-burger.burger")
      .event("click", SnabbdomUtil.update(lens.modify(!_)))
      .`class`("is-active", lens.get(state))
      .attr("aria-label", "menu")
      .attr("aria-expanded", "false")
      .child(Node("span").attr("aria-hidden", "true"))
      .child(Node("span").attr("aria-hidden", "true"))
      .child(Node("span").attr("aria-hidden", "true"))

  private def loginItem(implicit nutriaState: PageState) =
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
