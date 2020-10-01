package nutria.frontend

import nutria.frontend.service.UserService
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.{Snabbdom, SnabbdomFacade, VNode}

import scala.scalajs.js.|

class NutriaApp(container: Element) extends ExecutionContext {

  private var node: Element | VNode = container

  private val patch: SnabbdomFacade.PatchFunction = Snabbdom.init(
    classModule = true,
    attributesModule = true,
    styleModule = true,
    eventlistenersModule = true,
    propsModule = true
  )

  private def renderState(globalState: GlobalState, state: PageState): Unit = {
    val t0 = System.currentTimeMillis()

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

    val t1 = System.currentTimeMillis()

    val context = Context(state, globalState, renderState)
    val ui      = Pages.ui(context).toVNode

    val t2 = System.currentTimeMillis()

    node = patch(node, ui)

    val t3 = System.currentTimeMillis()

    dom.console.debug(s"""
         |Metric for ${state.getClass.getSimpleName}
         |saving state in history and location: ${t1 - t0}
         |rendering: ${t2 - t1}
         |patching: ${t3 - t2}
         |""".stripMargin.trim)
  }

  private def loadUserAndRenderFromLocation(): Unit =
    for {
      user <- UserService.whoAmI()
      globalState = GlobalState(user = user, navbarExpanded = false)
    } yield {
      renderState(globalState, Router.stateFromUrl(dom.window.location, globalState))
    }

  dom.window.onpopstate = _ => loadUserAndRenderFromLocation()

  loadUserAndRenderFromLocation()
}
