package nutria.frontend.ui

import nutria.frontend.{LoadingState, NutriaState}
import snabbdom._

object LoadingUi extends Page[LoadingState] {
  def render(implicit state: LoadingState, update: NutriaState => Unit) =
    Seq(common.Header(state, update), Node("progress.progress.is-primary.is-small.is-radiusless"))
}
