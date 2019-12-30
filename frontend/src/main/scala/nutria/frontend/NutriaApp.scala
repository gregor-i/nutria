package nutria.frontend

import nutria.frontend.ui.Ui
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.VNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.|
import scala.util.{Failure, Success}

class NutriaApp(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: NutriaState): Unit = {
    Router.stateToUrl(state) match {
      case Some((currentPath, currentSearch)) =>
        val stringSearch = Router.searchToUrl(currentSearch)
        if (dom.window.location.pathname != currentPath) {
          dom.window.scroll(0, 0)
          dom.window.history.pushState(null, "", currentPath + stringSearch)
        } else {
          dom.window.history.replaceState(null, "", currentPath + stringSearch)
        }
      case None => ()
    }

    state match {
      case LoadingState(future, _) =>
        future.onComplete {
          case Success(newState) => renderState(newState)
          case Failure(exception) =>
            renderState(
              ErrorState(s"unexpected problem while initializing app: ${exception.getMessage}")
            )
        }
      case _ => ()
    }

    node = patch(node, Ui(state, renderState).toVNode)
  }

  dom.window.onpopstate = _ => renderState(Router.stateFromUrl(dom.window.location))

  renderState(Router.stateFromUrl(dom.window.location))

}
