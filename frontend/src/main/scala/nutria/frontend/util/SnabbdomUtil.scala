package nutria.frontend.util

import snabbdom.{Snabbdom, SnabbdomFacade}

object SnabbdomUtil {
  def update[S](modify: S => S)(implicit state: S, update: S => Unit): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => update(modify(state)))

  def updateT[S](modify: S => S)(implicit updatable: Updatable[S, S]): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => updatable.update(modify(updatable.state)))

  val noop: SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => ())
}
