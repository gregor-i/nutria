package nutria.frontend.pages.common

import nutria.frontend.pages.LoadingState
import nutria.frontend.{ExecutionContext, NutriaState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future
import scala.util.chaining._

object Link extends ExecutionContext {
  def apply(newState: NutriaState)(implicit update: NutriaState => Unit): Node =
    Node("a")
      .key(newState.hashCode())
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        update(newState)
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

  def async(href: String, loadingState: => Future[NutriaState])(implicit state: NutriaState, update: NutriaState => Unit): Node = {
    Node("a")
      .attr("href", href)
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        update(LoadingState(state.user, loadingState))
      })
  }
}
