package nutria.frontend.pages
package common

import nutria.api.User
import nutria.frontend._
import nutria.frontend.util.SnabbdomUtil
import snabbdom.Node

object Header {
  def apply()(implicit context: Context[PageState]): Node = {
    "nav.navbar"
      .attr("role", "navigation")
      .attr("aria-label", "main navigation")
      .child(
        "div.navbar-brand"
          .child(brand)
          .child(burgerMenu())
      )
      .child(
        "div.navbar-menu"
          .`class`("is-active", context.global.navbarExpanded)
          .child(
            "div.navbar-start"
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
                context.global.user.map(
                  user =>
                    Link
                      .async(s"/user/${user.id}/gallery", Links.userGalleryState(user.id))
                      .classes("navbar-item")
                      .text("My Gallery")
                )
              )
              .childOptional(
                context.global.user.map(
                  _ =>
                    Link
                      .async("/templates", TemplateGalleryState.load(context.global))
                      .classes("navbar-item")
                      .text("My Templates")
                )
              )
              .childOptional(
                context.global.user.map(
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
                ).filter(_ => context.global.user.exists(_.admin))
              )
          )
          .child(
            "div.navbar-end"
              .child(
                context.global.user match {
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

  def loginHref(nutriaState: PageState): String =
    Router.stateToUrl(nutriaState) match {
      case None                 => "/auth/google"
      case Some((base, search)) => s"/auth/google?return-to=${base + Router.queryParamsToUrl(search)}"
    }

  val logoutHref: String = "/auth/logout"

  private val brand =
    "div.navbar-item"
      .child(
        Images(Images.icon)
          .style("height", "36px")
          .style("width", "36px")
          .style("max-height", "36px")
      )
      .child(
        "span"
          .style("font-size", "36px")
          .style("line-height", "36px")
          .style("color", "black")
          .style("margin-left", "4px")
          .text("Nutria")
      )

  private def burgerMenu()(implicit context: Context[_]) =
    "a.navbar-burger.burger"
      .event("click", SnabbdomUtil.modifyGlobal(GlobalState.navbarExpanded.modify(!_)))
      .`class`("is-active", context.global.navbarExpanded)
      .attr("aria-label", "menu")
      .attr("aria-expanded", "false")
      .child("span".attr("aria-hidden", "true"))
      .child("span".attr("aria-hidden", "true"))
      .child("span".attr("aria-hidden", "true"))

  private def loginItem(implicit context: Context[PageState]) =
    "div.navbar-item"
      .child(
        "a.button.is-rounded"
          .attr("href", loginHref(context.local))
          .child(Icons.icon(Icons.login))
          .child("span".text("Log in"))
      )

  private def logoutItem(user: User) =
    "div.navbar-item"
      .child(
        "a.button.is-rounded"
          .attr("href", logoutHref)
          .child(Icons.icon(Icons.logout))
          .child("span".text(s"Log out"))
      )
}
