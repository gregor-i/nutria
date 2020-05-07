package nutria.frontend.pages.common

import nutria.frontend.{NutriaState, Router}
import snabbdom.Node

object Body {
  def apply()(implicit nutriaState: NutriaState): Node =
    Node("nutria-app")
      .key(Router.stateToUrl(nutriaState).fold("")(_._1))
      .classes(nutriaState.getClass.getSimpleName)
}
