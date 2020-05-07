package nutria.frontend.pages

import nutria.core.User
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Footer, Header}
import nutria.macros.StaticContent
import snabbdom.Node

case class FAQState(
    user: Option[User],
    navbarExpanded: Boolean = false
) extends NutriaState {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object FAQPage extends Page[FAQState] {
  override def stateFromUrl: PartialFunction[(Path, QueryParameter), NutriaState] = {

    case ("/faq", _) =>
      LoadingState(
        Links.faqState()
      )

  }

  override def stateToUrl(state: FAQPage.State): Option[(Path, QueryParameter)] =
    Some("/faq" -> Map.empty)

  def render(implicit state: FAQState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(content())
      .child(Footer())

  private def content()(implicit state: FAQState, update: NutriaState => Unit) =
    Node("div.container")
      .prop("innerHTML", StaticContent("frontend/src/main/html/faq.html"))
}
