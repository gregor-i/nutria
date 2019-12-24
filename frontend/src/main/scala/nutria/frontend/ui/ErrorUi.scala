package nutria.frontend.ui

import nutria.frontend._
import snabbdom._

object ErrorUi {
  def render(implicit state: ErrorState, update: NutriaState => Unit): Node =
    Node("body")
      .key("error")
      .child(common.Header(state, update))
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
