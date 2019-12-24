package nutria.frontend.ui

import nutria.frontend.{LoadingState, NutriaState}
import snabbdom._

object LoadingUi {
  def render(implicit state: LoadingState, update: NutriaState => Unit): Node =
    Node("body")
      .key("loading")
      .child(common.Header(state, update))
      .child(Node("progress.progress.is-primary.is-small.is-radiusless"))
}
