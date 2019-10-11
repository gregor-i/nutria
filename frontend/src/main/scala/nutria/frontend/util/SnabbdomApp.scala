package nutria.frontend.util

import snabbdom.{Snabbdom, SnabbdomNative}


trait SnabbdomApp {
  val patch: SnabbdomNative.PatchFunction = Snabbdom.init(
    classModule = true,
    attributesModule = true,
    styleModule = true,
    eventlistenersModule = true,
    propsModule = true
  )
}

object SnabbdomApp
