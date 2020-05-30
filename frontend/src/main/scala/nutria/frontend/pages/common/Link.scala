package nutria.frontend.pages.common

import nutria.frontend.pages.LoadingState
import nutria.frontend.{ExecutionContext, NutriaState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future
import scala.util.chaining._

object Link extends ExecutionContext {
  def apply(newState: NutriaState)(implicit update: NutriaState => Unit): Node =
    Node("a")
    // todo: creating this href cost a lot of performance
      .pipe(
        link =>
          Router.stateToUrl(newState) match {
            case None                 => link
            case Some((path, search)) => link.attr("href", path + Router.queryParamsToUrl(search))
          }
      )
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        update(newState)
      })

  def async(href: String, loadingState: => Future[NutriaState])(implicit state: NutriaState, update: NutriaState => Unit): Node = {
    Node("a")
      .attr("href", href)
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        update(LoadingState(state.user, loadingState))
      })
  }
}
