package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import snabbdom.Node

@Lenses
case class ProfileState(
    about: User
) extends PageState

object ProfilePage extends Page[ProfileState] {

  override def stateFromUrl = {
    case (globalState, s"/user/profile", _) if globalState.user.isDefined =>
      ProfileState(about = globalState.user.get)
  }

  override def stateToUrl(state: ProfilePage.State): Option[(Path, QueryParameter)] =
    Some(s"/user/profile" -> Map.empty)

  def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(content())
      .child(Footer())

  def content()(implicit context: Context) =
    "div.container"
      .child(
        "section.section"
          .child("h1.title.is-1".text("User Profile"))
          .child("h2.subtitle".text("ID: " + context.local.about.id))
      )
      .child(
        "section.section".children(
          "h4.title.is-4".text("Saved information from Google:"),
          Form
            .readonlyStringInput("Google User Id", context.local.about.googleUserId.getOrElse("<None>")),
          Form.readonlyStringInput("Name", context.local.about.name),
          Form.readonlyStringInput("Email", context.local.about.email)
        )
      )
      .child(
        "section.section"
          .child(
            "div.field.is-grouped.is-grouped-right"
              .child(
                "p.control"
                  .child(
                    Button("Delete profile", Icons.delete, Actions.deleteUser(context.local.about.id))
                      .classes("is-danger")
                  )
              )
          )
      )
}
