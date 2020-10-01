package nutria.frontend.pages.common

import nutria.frontend.util.Updatable
import nutria.frontend.{PageState, Router}
import snabbdom.Node

object Body {
  def apply()(implicit updatable: Updatable[PageState, _]): Node =
    Node("nutria-app")
      .key(Router.stateToUrl(updatable.state).fold("")(_._1))
      .classes(updatable.state.getClass.getSimpleName)
}
