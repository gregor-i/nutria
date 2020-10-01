package nutria.frontend.pages.common

import nutria.frontend.Page.Local
import nutria.frontend.{PageState, Router}
import snabbdom.Node

object Body {
  def apply()(implicit local: Local[PageState]): Node =
    Node("nutria-app")
      .key(Router.stateToUrl(local.state).fold("")(_._1))
      .classes(local.state.getClass.getSimpleName)
}
