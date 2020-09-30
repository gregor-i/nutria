package nutria.frontend.util

import nutria.frontend.GlobalState
import snabbdom.{Snabbdom, SnabbdomFacade}

object SnabbdomUtil {
  def update[S](modify: S => S)(implicit globalState: GlobalState, state: S, update: S => Unit): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => update(modify(state)))

  val noop: SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => ())
}
