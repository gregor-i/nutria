package nutria.frontend.pages
package common

import nutria.frontend.{Context, ExecutionContext, PageState, Router}
import snabbdom.{Event, Node}

import scala.concurrent.Future

object Link extends ExecutionContext {
  def apply(newState: PageState)(implicit context: Context[_]): Node =
    "a"
      .key(newState.hashCode())
      .event[Event](
        "click",
        event => {
          event.preventDefault()
          context.update(context.global.copy(navbarExpanded = false), newState)
        }
      )
      .hookPostpatch({ (_, vnode) =>
        Future {
          for {
            (path, search) <- Router.stateToUrl(newState)
            elem           <- vnode.elm.toOption
          } yield elem.setAttribute("href", path + Router.queryParamsToUrl(search))
        }
      })

  def async(
      href: String,
      loadingState: => Future[PageState]
  )(implicit context: Context[_]): Node = {
    "a"
      .attr("href", href)
      .event[Event](
        "click",
        e => {
          e.preventDefault()
          context.update(context.global.copy(navbarExpanded = false), LoadingState(loadingState))
        }
      )
  }
}
