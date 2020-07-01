package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Footer, Header}
import nutria.macros.StaticContent
import snabbdom.Node

@Lenses
case class FAQState(
    user: Option[User],
    navbarExpanded: Boolean = false
) extends NutriaState

object FAQPage extends Page[FAQState] {
  override def stateFromUrl = {
    case (user, "/faq", _) =>
      Links.faqState(user)
  }

  override def stateToUrl(state: FAQPage.State): Option[(Path, QueryParameter)] =
    Some("/faq" -> Map.empty)

  def render(implicit state: FAQState, update: NutriaState => Unit) =
    Body()
      .child(Header(FAQState.navbarExpanded))
      .child(content())
      .child(Footer())

  private def content()(implicit state: FAQState, update: NutriaState => Unit) =
    Node("div.container")
      .prop("innerHTML", StaticContent("frontend/src/main/html/faq.html"))
}
