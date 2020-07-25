package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Footer, Header}
import nutria.macros.StaticContent
import snabbdom.Node

@Lenses
case class DocumentationState(
    user: Option[User],
    subpage: Subpage,
    navbarExpanded: Boolean = false
) extends NutriaState

object DocumentationState {
  def faq(implicit nutriaState: NutriaState)          = DocumentationState(user = nutriaState.user, subpage = FAQ)
  def introduction(implicit nutriaState: NutriaState) = DocumentationState(user = nutriaState.user, subpage = Introduction)
}

sealed trait Subpage
case object FAQ          extends Subpage
case object Introduction extends Subpage

object DocumentationPage extends Page[DocumentationState] {

  override def stateFromUrl = {
    case (user, "/documentation/faq", _) =>
      DocumentationState(user = user, subpage = FAQ)
    case (user, "/documentation/introduction", _) =>
      DocumentationState(user = user, subpage = Introduction)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    state.subpage match {
      case FAQ =>
        Some("/documentation/faq" -> Map.empty)
      case Introduction =>
        Some("/documentation/introduction" -> Map.empty)
    }

  def render(implicit state: State, update: NutriaState => Unit) =
    Body()
      .child(Header(DocumentationState.navbarExpanded))
      .child(content(state.subpage))
      .child(Footer())

  private def content(subpage: Subpage)(implicit state: DocumentationState, update: NutriaState => Unit) =
    subpage match {
      case FAQ =>
        Node("div.container")
          .prop("innerHTML", StaticContent("frontend/src/main/html/faq.html"))
      case Introduction =>
        Node("div.container")
          .prop("innerHTML", StaticContent("frontend/src/main/html/introduction.html"))
    }

}
