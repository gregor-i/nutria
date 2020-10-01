package nutria.frontend
package pages

import monocle.macros.Lenses
import nutria.api.FractalTemplateEntityWithId
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common._
import nutria.frontend.service.TemplateService
import snabbdom.{Node, Snabbdom}

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

  override def render(implicit global: Global, local: Local): Node =
    Body()
      .child(Header())
      .child(
        Header
          .fab(Node("button"))
          .child(Icons.icon(Icons.plus))
          .event("click", Snabbdom.event(_ => local.update(TemplateEditorState.initial)))
      )
      .child(body())
      .child(Footer())

  def body()(implicit global: Global, local: Local): Node =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text("Template Gallery:"))
      )
      .child(table(local.state.templates))

  private def table(
      templates: Seq[FractalTemplateEntityWithId]
  )(implicit global: Global, local: Local): Node =
    Node("section.section")
      .child(
        Node("table.table.is-fullwidth")
          .child(
            Node("tr")
              .child(Node("th").text("Title"))
              .child(Node("th").text("State"))
              .child(Node("th"))
          )
          .child(
            templates.map(
              template =>
                Node("tr")
                  .child(Node("td").text(template.entity.title))
                  .child(Node("td").text(if (template.entity.published) "published" else "private"))
                  .child(
                    Node("td").child(
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
