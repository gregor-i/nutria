package nutria.frontend.ui

import nutria.frontend.{LoadingState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom.VNode

object LoadingUi {
  def render(implicit state: LoadingState, update: NutriaState => Unit): VNode =
    h("body",
      key = "loading")(
      common.Header(state, update),
      h("progress.progress.is-primary.is-small.is-radiusless")()
    )

}
