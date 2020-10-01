package nutria.frontend.pages.common

import nutria.frontend.pages.LoadingState
import nutria.frontend.util.Updatable
import nutria.frontend.{ExecutionContext, GlobalState, PageState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future
import scala.util.chaining._

object Link extends ExecutionContext {
  def apply(newState: PageState)(implicit updatable: Updatable[_, PageState]): Node =
    Node("a")
      .key(newState.hashCode())
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        updatable.update(newState)
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
  )(implicit updatable: Updatable[_, PageState]): Node = {
    Node("a")
      .attr("href", href)
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        updatable.update(LoadingState(loadingState))
      })
  }
}
