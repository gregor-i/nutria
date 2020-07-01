package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import snabbdom.Node

@Lenses
case class ProfileState(
    about: User,
    navbarExpanded: Boolean = false
) extends NutriaState {
  def user: Some[User] = Some(about)
}

object ProfilePage extends Page[ProfileState] {

  override def stateFromUrl = {
    case (Some(user), s"/user/profile", _) =>
      ProfileState(about = user)
  }

  override def stateToUrl(state: ProfilePage.State): Option[(Path, QueryParameter)] =
    Some(s"/user/profile" -> Map.empty)

  def render(implicit state: ProfileState, update: NutriaState => Unit): Node =
    Body()
      .child(Header(ProfileState.navbarExpanded))
      .child(content())
      .child(Footer())

  def content()(implicit state: ProfileState, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text("User Profile"))
          .child(Node("h2.subtitle").text("ID: " + state.about.id))
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Saved information from Google:"),
          Form
            .readonlyStringInput("Google User Id", state.about.googleUserId.getOrElse("<None>")),
          Form.readonlyStringInput("Name", state.about.name),
          Form.readonlyStringInput("Email", state.about.email)
        )
      )
      .child(
        Node("section.section")
          .child(
            Node("div.field.is-grouped.is-grouped-right")
              .child(
                Node("p.control")
                  .child(
                    Button("Delete profile", Icons.delete, Actions.deleteUser(state.about.id))
                      .classes("is-danger")
                  )
              )
          )
      )
}
