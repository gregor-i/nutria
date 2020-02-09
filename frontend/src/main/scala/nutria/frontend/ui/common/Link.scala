package nutria.frontend.ui.common

import nutria.frontend.{NutriaState, Router}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Link {
  def apply(newState: NutriaState)(implicit update: NutriaState => Unit): Node = {
    val href = Router.stateToUrl(newState) match {
      case None                 => null
      case Some((path, search)) => path + Router.searchToUrl(search)
    }

    Node("a")
      .attr("href", href)
      .event("input", Snabbdom.event { e =>
        e.preventDefault()
        update(newState)
      })
  }

  def async(href: String, state: Future[NutriaState])(implicit update: NutriaState => Unit): Node = {
    Node("a")
      .attr("href", href)
      .event("input", Snabbdom.event { e =>
        e.preventDefault()
        state.foreach(update)
      })
  }
}
