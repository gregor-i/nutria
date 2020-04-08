package nutria.frontend

import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.{Snabbdom, SnabbdomFacade, VNode}

import scala.scalajs.js.|

class NutriaApp(container: Element) {

  var node: Element | VNode = container

  val patch: SnabbdomFacade.PatchFunction = Snabbdom.init(
    classModule = true,
    attributesModule = true,
    styleModule = true,
    eventlistenersModule = true,
    propsModule = true
  )

  def renderState(state: NutriaState): Unit = {
    Router.stateToUrl(state) match {
      case Some((currentPath, currentSearch)) =>
        val stringSearch = Router.queryParamsToUrl(currentSearch)
        if (dom.window.location.pathname != currentPath) {
          dom.window.scroll(0, 0)
          dom.window.history.pushState(null, "", currentPath + stringSearch)
        } else {
          dom.window.history.replaceState(null, "", currentPath + stringSearch)
        }
      case None => ()
    }

    node = patch(node, Pages.ui(state, renderState).toVNode)
  }

  dom.window.onpopstate = _ => renderState(Router.stateFromUrl(dom.window.location))

  renderState(Router.stateFromUrl(dom.window.location))
}
