package nutria.frontend.pages

import nutria.api.{FractalEntity, FractalTemplateEntityWithId, User, WithId}
import nutria.core.FractalTemplate
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.AdminPage.action
import nutria.frontend.pages.common.{Body, Button, Footer, Header, Icons, Link}
import nutria.frontend.service.{NutriaAdminService, NutriaService}
import nutria.frontend.{ExecutionContext, NutriaState, Page}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future

case class TemplateGalleryState(
    templates: Seq[FractalTemplateEntityWithId],
    user: Option[User],
    navbarExpanded: Boolean = false
) extends NutriaState {
  def setNavbarExtended(boolean: Boolean) = copy(navbarExpanded = boolean)
}

object TemplateGalleryState extends ExecutionContext {
  def load(): Future[TemplateGalleryState] =
    for {
      user      <- NutriaService.whoAmI()
      templates <- NutriaService.loadAllTemplates()
    } yield TemplateGalleryState(templates = templates, user = user)
}

object TemplateGalleryPage extends Page[TemplateGalleryState] {
  override def stateFromUrl: PartialFunction[(Path, QueryParameter), NutriaState] = {
    case ("/templates", _) => LoadingState(TemplateGalleryState.load())
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some("/templates" -> Map.empty)

  override def render(implicit state: State, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
      .child(
        Header
          .fab(Node("button"))
          .child(Icons.icon(Icons.plus))
          .event("click", Snabbdom.event(_ => update(TemplateEditorState.initial)))
      )
      .child(body)
      .child(Footer())

  def body()(implicit state: State, update: NutriaState => Unit): Node =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text("Template Gallery:"))
      )
      .child(table(state.templates))

  private def table(templates: Seq[FractalTemplateEntityWithId])(implicit state: State, update: NutriaState => Unit): Node =
    Node("section.section")
      .child(
        Node("table.table.is-fullwidth")
          .child(
            Node("tr")
              .child(Node("th").text("Id"))
              .child(Node("th").text("Title"))
              .child(Node("th").text("Description"))
              .child(Node("th").text("Published"))
              .child(Node("th"))
          )
          .child(
            templates.map(
              template =>
                Node("tr")
                  .child(Node("td").text(template.id))
                  .child(Node("td").text(template.entity.title))
                  .child(Node("td").text(template.entity.description))
                  .child(Node("td").text(template.entity.published.toString))
                  .child(
                    Node("td").child(
                      Link(TemplateEditorState.byTemplate(template))
                        .classes("button")
                        .child(Icons.icon(Icons.edit))
                        .child(Node("span").text("Edit"))
                    )
                  )
            )
          )
      )

}
