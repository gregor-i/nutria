package nutria.frontend.pages.common

import nutria.frontend.{Context, PageState, Router}
import snabbdom.Node

object Body {
  def apply()(implicit context: Context[PageState]): Node =
    Node("nutria-app")
      .key(Router.stateToUrl(context.local).fold("")(_._1))
      .classes(context.local.getClass.getSimpleName)
}
