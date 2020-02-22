package nutria.frontend.ui

import nutria.frontend._
import nutria.frontend.ui.common.{Body, Button, Footer, Form, Header, Icons}
import snabbdom.{Node, _}

object ProfileUi extends Page[ProfileState] {
  def render(implicit state: ProfileState, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
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
                    Button("Delete user", Icons.delete, Actions.deleteUser(state.about.id))
                      .classes("is-danger")
                  )
              )
          )
      )
}
