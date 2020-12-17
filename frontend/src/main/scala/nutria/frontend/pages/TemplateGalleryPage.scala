package nutria.frontend
package pages

import monocle.macros.Lenses
import nutria.api.FractalTemplateEntityWithId
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common._
import nutria.frontend.service.TemplateService
import snabbdom.{Event, Node}

import scala.concurrent.Future

@Lenses
case class TemplateGalleryState(
    templates: Seq[FractalTemplateEntityWithId]
) extends PageState

object TemplateGalleryState extends ExecutionContext {
  def load(globalState: GlobalState): Future[PageState] =
    globalState.user match {
      case Some(user) =>
        for {
          templates <- TemplateService.listUser(user.id)
        } yield TemplateGalleryState(templates = templates)
      case None =>
        Future.successful(ErrorState.unauthorized)
    }
}

object TemplateGalleryPage extends Page[TemplateGalleryState] {
  override def stateFromUrl = {
    case (globalState, "/templates", _) => TemplateGalleryState.load(globalState).loading()
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some("/templates" -> Map.empty)

  def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(
        Header
          .fab("button")
          .child(Icons.icon(Icons.plus))
          .event[Event]("click", _ => context.update(TemplateEditorState.initial))
      )
      .child(body())
      .child(Footer())

  def body()(implicit context: Context): Node =
    "div.container"
      .child(
        "section.section"
          .child("h1.title.is-1".text("Template Gallery:"))
      )
      .child(table(context.local.templates))

  private def table(
      templates: Seq[FractalTemplateEntityWithId]
  )(implicit context: Context): Node =
    "section.section"
      .child(
        "table.table.is-fullwidth"
          .child(
            "tr"
              .child("th".text("Title"))
              .child("th".text("State"))
              .child("th")
          )
          .child(
            templates.map(
              template =>
                "tr"
                  .child("td".text(template.entity.title))
                  .child("td".text(if (template.entity.published) "published" else "private"))
                  .child(
                    "td".child(
                      ButtonList(
                        Link(TemplateEditorState.byTemplate(template))
                          .classes("button", "is-rounded")
                          .child(Icons.icon(Icons.edit)),
                        Button.icon(if (template.entity.published) Icons.unpublish else Icons.publish, Actions.togglePublishedTemplate(template)),
                        Button.icon(Icons.delete, Actions.deleteTemplate(template.id))
                      )
                    )
                  )
            )
          )
      )

}
