package nutria.frontend.ui

import nutria.frontend._
import nutria.frontend.ui.common.{Body, Header}
import snabbdom._

object ErrorUi extends Page[ErrorState] {
  def render(implicit state: ErrorState, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
      .child(
        Node("div.section")
          .child(
            Node("article.message.is-danger")
              .child(
                Node("div.message-body")
                  .child(Node("div.title").text("An unexpected error occured."))
                  .child(Node("div.subtitle").text(state.message))
                  .child(Node("a").attr("href", "/").text("return to landing page"))
              )
          )
      )
}
