package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api._
import nutria.core.{FractalImage, FractalTemplate}
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common._
import nutria.frontend.service.NutriaAdminService
import nutria.frontend.{GlobalState, Page, PageState}
import snabbdom.components.{Button, ButtonList}
import snabbdom.{Event, Node}

import scala.concurrent.Future

@Lenses
case class AdminState(
    users: Vector[User],
    fractals: Vector[WithId[Option[Entity[FractalImage]]]],
    templates: Vector[WithId[Option[Entity[FractalTemplate]]]]
) extends PageState

object AdminState {
  def initial() = NutriaAdminService.load()
}

object AdminPage extends Page[AdminState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (globalState, "/admin", _) if globalState.user.exists(_.admin) => AdminState.initial().loading()
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some("/admin" -> Map.empty)

  override def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(
        "div.container"
          .child("section.section".child("h1.title.is-1".text("Admin:")))
          .child(usersTable(context.local.users))
          .child(fractalsTable(context.local.fractals))
          .child(templatesTable(context.local.templates))
          .child(actionBar())
      )

  private def usersTable(users: Seq[User])(implicit context: Context): Node =
    "section.section"
      .child("h4.title.is-4".text(s"Users: ${users.length}"))
      .child(
        "table.table.is-fullwidth"
          .child(
            "tr"
              .child("th".text("Id"))
              .child("th".text("Name"))
              .child("th".text("Email"))
              .child("th".text("Google User Id"))
              .child("th")
          )
          .child(
            users.map(user =>
              "tr"
                .child("td".text(user.id))
                .child("td".text(user.name))
                .child("td".text(user.email))
                .child("td".text(user.googleUserId.getOrElse("<None>")))
                .child(
                  "td".child(
                    Button.icon(Icons.delete, action(NutriaAdminService.deleteUser(user.id)))
                  )
                )
            )
          )
      )

  private def fractalsTable(fractals: Seq[WithId[Option[FractalImageEntity]]])(implicit context: Context): Node =
    "section.section"
      .child("h4.title.is-4".text(s"Fractals: ${fractals.length}"))
      .child(
        "table.table.is-fullwidth"
          .child(
            "tr"
              .child("th".text("Id"))
              .child("th".text("Owner"))
              .child("th".text("Published"))
              .child("th".text("Title"))
              .child("th".text("Description"))
              .child("th")
          )
          .child(
            fractals.map(fractal =>
              "tr"
                .child("td".text(fractal.id))
                .child("td".text(fractal.owner))
                .child("td".text(fractal.entity.fold("invalid")(_.published.toString)))
                .child("td".text(fractal.entity.fold("invalid")(_.title)))
                .child("td".text(fractal.entity.fold("invalid")(_.description)))
                .child(
                  "td".child(
                    Button.icon(Icons.delete, action(NutriaAdminService.deleteFractal(fractal.id)))
                  )
                )
            )
          )
      )

  private def templatesTable(
      templates: Seq[WithId[Option[FractalTemplateEntity]]]
  )(implicit context: Context): Node =
    "section.section"
      .child("h4.title.is-4".text(s"Templates: ${templates.length}"))
      .child(
        "table.table.is-fullwidth"
          .child(
            "tr"
              .child("th".text("Id"))
              .child("th".text("Owner"))
              .child("th".text("Published"))
              .child("th".text("Title"))
              .child("th".text("Description"))
              .child("th")
          )
          .child(
            templates.map(template =>
              "tr"
                .child("td".text(template.id))
                .child("td".text(template.owner))
                .child("td".text(template.entity.fold("invalid")(_.published.toString)))
                .child("td".text(template.entity.fold("invalid")(_.title)))
                .child("td".text(template.entity.fold("invalid")(_.description)))
                .child("td")
            )
          )
      )

  private def actionBar()(implicit context: Context): Node =
    "section.section"
      .child(
        ButtonList.right(
          Button("clean Fractals", action(NutriaAdminService.cleanFractals())),
          Button("delete all Fractals", action(NutriaAdminService.truncateFractals())),
          Button("insert examples", action(NutriaAdminService.insertExamples())),
          Button("migrate Fractals", action(NutriaAdminService.migrateFractals()))
        )
      )

  private def action(
      action: => Future[Unit]
  )(implicit context: Context): Event => Unit =
    (_: Event) -> action.flatMap(_ => NutriaAdminService.load()).foreach(context.update)
}
