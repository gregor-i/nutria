package nutria.frontend.ui

import nutria.core.{FractalEntityWithId, User}
import nutria.frontend.service.NutriaAdminService
import nutria.frontend.ui.common.{Button, Header, Icons}
import nutria.frontend.{AdminState, NutriaState}
import snabbdom.{Node, Snabbdom, SnabbdomFacade}

import scala.concurrent.Future

import NutriaAdminService.ex

object AdminUi extends Page[AdminState] {
  def render(implicit state: AdminState, update: NutriaState => Unit): Seq[Node] =
    Seq(
      Header.apply,
      Node("div.container")
        .child(Node("h1.title.is-1").text("Admin:"))
        .child(usersTable(state.users))
        .child(fractalsTable(state.fractals))
        .child(actionBar())
    )

  private def usersTable(users: Seq[User])(implicit update: NutriaState => Unit): Node =
    Node("section.section")
      .child(Node("h4.title.is-4").text(s"Users: ${users.length}"))
      .child(
        Node("table.table.is-fullwidth")
          .child(
            Node("tr")
              .child(Node("th").text("Id"))
              .child(Node("th").text("Name"))
              .child(Node("th").text("Email"))
              .child(Node("th").text("Google User Id"))
              .child(Node("th"))
          )
          .child(
            users.map(
              user =>
                Node("tr")
                  .child(Node("td").text(user.id))
                  .child(Node("td").text(user.name))
                  .child(Node("td").text(user.email))
                  .child(Node("td").text(user.googleUserId.getOrElse("<None>")))
                  .child(
                    Node("td").child(
                      Button
                        .icon(Icons.delete, action(NutriaAdminService.deleteUser(user.id)))
                    )
                  )
            )
          )
      )

  private def fractalsTable(
      fractals: Seq[FractalEntityWithId]
  )(implicit update: NutriaState => Unit): Node =
    Node("section.section")
      .child(Node("h4.title.is-4").text(s"Fractals: ${fractals.length}"))
      .child(
        Node("table.table.is-fullwidth")
          .child(
            Node("tr")
              .child(Node("th").text("Id"))
              .child(Node("th").text("Owner"))
              .child(Node("th").text("Published"))
              .child(Node("th").text("Title"))
              .child(Node("th").text("Description"))
              .child(Node("th").text("Typ"))
              .child(Node("th"))
          )
          .child(
            fractals.map(
              fractal =>
                Node("tr")
                  .child(Node("td").text(fractal.id))
                  .child(Node("td").text(fractal.owner))
                  .child(Node("td").text(fractal.entity.published.toString))
                  .child(Node("td").text(fractal.entity.title))
                  .child(Node("td").text(fractal.entity.description))
                  .child(Node("td").text(fractal.entity.program.getClass.getSimpleName))
                  .child(
                    Node("td").child(
                      Button
                        .icon(Icons.delete, action(NutriaAdminService.deleteFractal(fractal.id)))
                    )
                  )
            )
          )
      )

  private def actionBar()(implicit update: NutriaState => Unit): Node =
    Node("section.section")
      .child(
        Node("div.buttons.is-right")
          .child(Button("Insert system Fractals", action(NutriaAdminService.insertSystemFractals())))
          .child(Button("clean Fractals", action(NutriaAdminService.cleanFractals())))
          .child(Button("delete all Fractals", action(NutriaAdminService.truncateFractals())))
      )

  private def action(
      action: => Future[Unit]
  )(implicit update: NutriaState => Unit): SnabbdomFacade.Eventlistener =
    Snabbdom.event { _ -> action.flatMap(_ => NutriaAdminService.load()).foreach(update) }
}
