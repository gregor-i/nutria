package nutria.frontend.ui

import nutria.frontend._
import snabbdom.Snabbdom.h
import snabbdom.VNode

object ErrorUi {
  def render(implicit state: ErrorState, update: NutriaState => Unit): VNode =
    h("body",
      key = "error")(
      common.Header("Nutria Fractal")(state, update),
      h("div.section")(
        h("article.message.is-danger")(
          h("div.message-body")(
            h("div.title")("An unexpected error occured."),
            h("div.subtitle")(state.message),
            h("a", attrs = Seq("href" -> "/"))("return to lobby"),
          )
        )
      )
    )

}
