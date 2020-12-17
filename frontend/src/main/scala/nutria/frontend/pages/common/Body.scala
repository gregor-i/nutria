package nutria.frontend.pages
package common

import nutria.frontend.{Context, PageState, Router}
import snabbdom.Node

object Body {
  def apply()(implicit context: Context[PageState]): Node =
    "nutria-app"
      .key(Router.stateToUrl(context.local).fold("")(_._1))
      .classes(context.local.getClass.getSimpleName)
}
