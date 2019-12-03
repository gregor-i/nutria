package nutria.frontend.util

import snabbdom.{Snabbdom, SnabbdomFacade}


trait SnabbdomApp {
  val patch: SnabbdomFacade.PatchFunction = Snabbdom.init(
    classModule = true,
    attributesModule = true,
    styleModule = true,
    eventlistenersModule = true,
    propsModule = true
  )
}

object SnabbdomApp
