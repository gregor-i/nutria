package nutria.frontend.ui

import nutria.frontend.ui.common.{Body, Header}
import nutria.frontend.{LoadingState, NutriaState}
import snabbdom._

object LoadingUi extends Page[LoadingState] {
  def render(implicit state: LoadingState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(Node("progress.progress.is-primary.is-small.is-radiusless"))
}
