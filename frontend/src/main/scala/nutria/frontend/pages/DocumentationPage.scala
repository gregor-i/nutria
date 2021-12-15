package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Footer, Header}
import nutria.macros.StaticContent
import snabbdom.Node

@Lenses
case class DocumentationState(
    subpage: Subpage
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

  def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(content(context.local.subpage))
      .child(Footer())

  private def content(subpage: Subpage) =
    subpage match {
      case FAQ =>
        "div.container"
          .prop("innerHTML", StaticContent("frontend/src/main/html/faq.html"))
      case Introduction =>
        "div.container"
          .prop("innerHTML", StaticContent("frontend/src/main/html/introduction.html"))
    }
}
