package nutria.frontend.ui

import nutria.frontend._
import nutria.frontend.ui.common.{Body, Footer, Header}
import nutria.macros.StaticContent
import snabbdom.Node

object FAQUi extends Page[FAQState] {
  def render(implicit state: FAQState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(content())
      .child(Footer())

  private def content()(implicit state: FAQState, update: NutriaState => Unit) =
    Node("div.container")
      .prop("innerHTML", StaticContent("frontend/src/main/html/faq.html"))
}
