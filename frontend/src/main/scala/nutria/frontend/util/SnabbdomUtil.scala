package nutria.frontend.util

import snabbdom.{Snabbdom, SnabbdomFacade}

object SnabbdomUtil {
  def update[S](modify: S => S)(implicit state: S, update: S => Unit): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => update(modify(state)))

}
