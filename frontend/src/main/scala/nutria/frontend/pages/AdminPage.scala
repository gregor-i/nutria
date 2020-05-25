package nutria.frontend.pages

import nutria.api.{Entity, FractalEntity, FractalTemplateEntityWithId, User, WithId}
import nutria.core.{Fractal, FractalImage, FractalTemplate}
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{Body, Button, ButtonList, Header, Icons}
import nutria.frontend.service.NutriaAdminService
import nutria.frontend.service.NutriaAdminService.ex
import nutria.frontend.{NutriaState, Page}
import snabbdom.{Node, Snabbdom, SnabbdomFacade}

import scala.concurrent.Future

case class AdminState(
    admin: User,
    users: Vector[User],
    fractals: Vector[WithId[Option[Entity[FractalImage]]]],
    templates: Vector[WithId[Option[Entity[FractalTemplate]]]],
    navbarExpanded: Boolean = false
) extends NutriaState {
  def user: Some[User]                                          = Some(admin)
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object AdminState {
  def initial = LoadingState(NutriaAdminService.load())
}

object AdminPage extends Page[AdminState] {
  override def stateFromUrl: PartialFunction[(Path, QueryParameter), NutriaState] = {
    case ("/admin", _) => AdminState.initial
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some("/admin" -> Map.empty)

  def render(implicit state: State, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
      .child(
        Node("div.container")
          .child(Node("section.section").child(Node("h1.title.is-1").text("Admin:")))
          .child(usersTable(state.users))
          .child(fractalsTable(state.fractals))
          .child(templatesTable(state.templates))
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
      fractals: Seq[WithId[Option[Entity[FractalImage]]]]
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
              .child(Node("th"))
          )
          .child(
            fractals.map(
              fractal =>
                Node("tr")
                  .child(Node("td").text(fractal.id))
                  .child(Node("td").text(fractal.owner))
                  .child(Node("td").text(fractal.entity.fold("invalid")(_.published.toString)))
                  .child(Node("td").text(fractal.entity.fold("invalid")(_.title)))
                  .child(Node("td").text(fractal.entity.fold("invalid")(_.description)))
                  .child(
                    Node("td").child(
                      Button
                        .icon(Icons.delete, action(NutriaAdminService.deleteFractal(fractal.id)))
                    )
                  )
            )
          )
      )

  private def templatesTable(
      templates: Seq[WithId[Option[Entity[FractalTemplate]]]]
  )(implicit update: NutriaState => Unit): Node =
    Node("section.section")
      .child(Node("h4.title.is-4").text(s"Templates: ${templates.length}"))
      .child(
        Node("table.table.is-fullwidth")
          .child(
            Node("tr")
              .child(Node("th").text("Id"))
              .child(Node("th").text("Owner"))
              .child(Node("th").text("Published"))
              .child(Node("th").text("Title"))
              .child(Node("th").text("Description"))
              .child(Node("th"))
          )
          .child(
            templates.map(
              template =>
                Node("tr")
                  .child(Node("td").text(template.id))
                  .child(Node("td").text(template.owner))
                  .child(Node("td").text(template.entity.fold("invalid")(_.published.toString)))
                  .child(Node("td").text(template.entity.fold("invalid")(_.title)))
                  .child(Node("td").text(template.entity.fold("invalid")(_.description)))
                  .child(Node("td"))
            )
          )
      )

  private def actionBar()(implicit update: NutriaState => Unit): Node =
    Node("section.section")
      .child(
        ButtonList(
          Button("clean Fractals", action(NutriaAdminService.cleanFractals())),
          Button("delete all Fractals", action(NutriaAdminService.truncateFractals())),
          Button("insert examples", action(NutriaAdminService.insertExamples())),
          Button("migrate Fractals", action(NutriaAdminService.migrateFractals()))
        )
      )

  private def action(
      action: => Future[Unit]
  )(implicit update: NutriaState => Unit): SnabbdomFacade.Eventlistener =
    Snabbdom.event { _ -> action.flatMap(_ => NutriaAdminService.load()).foreach(update) }
}
