package nutria.frontend.pages.common

import nutria.frontend.pages.LoadingState
import nutria.frontend.{Context, ExecutionContext, PageState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future

object Link extends ExecutionContext {
  def apply(newState: PageState)(implicit context: Context[_]): Node =
    Node("a")
      .key(newState.hashCode())
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        context.update(newState)
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
  )(implicit context: Context[_]): Node = {
    Node("a")
      .attr("href", href)
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        context.update(LoadingState(loadingState))
      })
  }
}
