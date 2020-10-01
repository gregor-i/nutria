package nutria.frontend.pages.common

import nutria.frontend.Page.Local
import nutria.frontend.pages.LoadingState
import nutria.frontend.{ExecutionContext, PageState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future

object Link extends ExecutionContext {
  def apply(newState: PageState)(implicit local: Local[_]): Node =
    Node("a")
      .key(newState.hashCode())
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        local.update(newState)
      })
      .hook(
        "postpatch",
        Snabbdom.hook { (_, vnode) =>
          Future {
            for {
              (path, search) <- Router.stateToUrl(newState)
              elem           <- vnode.elm.toOption
            } yield elem.setAttribute("href", path + Router.queryParamsToUrl(search))
          }
        }
      )

  def async(
      href: String,
      loadingState: => Future[PageState]
  )(implicit local: Local[_]): Node = {
    Node("a")
      .attr("href", href)
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        local.update(LoadingState(loadingState))
      })
  }
}
