package nutria.frontend.ui

import nutria.frontend._
import snabbdom.Node

object Ui {
  def apply(nutriaState: NutriaState, update: NutriaState => Unit): Node = {
    Node("nutria-app")
      .key(Router.stateToUrl(nutriaState).fold("")(_._1))
      .children(nutriaState match {
        case state: ErrorState       => ErrorUi.render(state, update)
        case state: DetailsState     => DetailsUi.render(state, update)
        case state: ExplorerState    => ExplorerUi.render(state, update)
        case state: GalleryState     => GalleryUi.render(state, update)
        case state: UserGalleryState => UserGalleryUi.render(state, update)
        case state: LoadingState     => LoadingUi.render(state, update)
        case state: GreetingState    => GreetingUi.render(state, update)
        case state: ProfileState     => ProfileUi.render(state, update)
        case state: AdminState       => AdminUi.render(state, update)
      })
  }
}
