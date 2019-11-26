package nutria.frontend.ui

import nutria.frontend.{DetailsState, ErrorState, ExplorerState, LibraryState, NutriaState}
import snabbdom.VNode

object Ui {
  def apply(nutriaState: NutriaState, update: NutriaState => Unit): VNode = nutriaState match {
    case state:ErrorState => ErrorUi.render(state, update)
    case state:DetailsState => DetailsUi.render(state, update)
    case state:ExplorerState => ExplorerUi.render(state, update)
    case state:LibraryState => LibraryUi.render(state, update)
  }
}