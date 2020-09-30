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
    subpage: Subpage,
    navbarExpanded: Boolean = false
) extends PageState

object DocumentationState {
  val faq          = DocumentationState(subpage = FAQ)
  val introduction = DocumentationState(subpage = Introduction)
}

sealed trait Subpage
case object FAQ          extends Subpage
case object Introduction extends Subpage

object DocumentationPage extends Page[DocumentationState] {

  override def stateFromUrl = {
    case (user, "/documentation/faq", _) =>
      DocumentationState(subpage = FAQ)
    case (user, "/documentation/introduction", _) =>
      DocumentationState(subpage = Introduction)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    state.subpage match {
      case FAQ =>
        Some("/documentation/faq" -> Map.empty)
      case Introduction =>
        Some("/documentation/introduction" -> Map.empty)
    }

  override def render(implicit globalState: GlobalState, state: State, update: PageState => Unit) =
    Body()
      .child(Header(DocumentationState.navbarExpanded))
      .child(content(state.subpage))
      .child(Footer())

  private def content(subpage: Subpage)(implicit globalState: GlobalState, state: DocumentationState, update: PageState => Unit) =
    subpage match {
      case FAQ =>
        Node("div.container")
          .prop("innerHTML", StaticContent("frontend/src/main/html/faq.html"))
      case Introduction =>
        Node("div.container")
          .prop("innerHTML", StaticContent("frontend/src/main/html/introduction.html"))
    }

}
