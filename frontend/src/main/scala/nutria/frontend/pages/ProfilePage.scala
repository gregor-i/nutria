package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import nutria.frontend.util.Updatable
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

  override def render(implicit global: Global, local: Local): Node =
    Body()
      .child(Header())
      .child(content())
      .child(Footer())

  def content()(implicit global: Global, local: Local) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text("User Profile"))
          .child(Node("h2.subtitle").text("ID: " + local.state.about.id))
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Saved information from Google:"),
          Form
            .readonlyStringInput("Google User Id", local.state.about.googleUserId.getOrElse("<None>")),
          Form.readonlyStringInput("Name", local.state.about.name),
          Form.readonlyStringInput("Email", local.state.about.email)
        )
      )
      .child(
        Node("section.section")
          .child(
            Node("div.field.is-grouped.is-grouped-right")
              .child(
                Node("p.control")
                  .child(
                    Button("Delete profile", Icons.delete, Actions.deleteUser(local.state.about.id))
                      .classes("is-danger")
                  )
              )
          )
      )
}
