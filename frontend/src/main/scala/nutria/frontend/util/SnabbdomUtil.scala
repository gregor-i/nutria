package nutria.frontend.util

import snabbdom.{Snabbdom, SnabbdomFacade}

object SnabbdomUtil {
  def modify[S](modify: S => S)(implicit updatable: Updatable[S, S]): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => updatable.update(modify(updatable.state)))

  val noop: SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => ())
}
