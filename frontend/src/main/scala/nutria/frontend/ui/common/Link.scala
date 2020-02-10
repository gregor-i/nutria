package nutria.frontend.ui.common

import nutria.frontend.{NutriaState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.chaining._

object Link {
  def apply(newState: NutriaState)(implicit update: NutriaState => Unit): Node =
    Node("a")
      .pipe(
        link =>
          Router.stateToUrl(newState) match {
            case None                 => link
            case Some((path, search)) => link.attr("href", path + Router.searchToUrl(search))
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
