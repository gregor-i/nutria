package nutria.frontend.pages.common

import nutria.frontend.{ExecutionContext, NutriaState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future
import scala.util.chaining._

object Link extends ExecutionContext {
  def apply(newState: NutriaState)(implicit update: NutriaState => Unit): Node =
    Node("a")
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

  def async(href: String, state: => Future[NutriaState])(implicit update: NutriaState => Unit): Node = {
    Node("a")
      .attr("href", href)
      .event("click", Snabbdom.event { e =>
        e.preventDefault()
        state.foreach(update)
      })
  }
}
