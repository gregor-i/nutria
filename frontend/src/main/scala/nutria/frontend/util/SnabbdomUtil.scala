package nutria.frontend.util

import nutria.frontend.{Context, GlobalState}
import snabbdom.{Snabbdom, SnabbdomFacade}

object SnabbdomUtil {
  def modify[S](modify: S => S)(implicit updatable: Updatable[S, S]): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => updatable.update(modify(updatable.state)))

  def modifyGlobal(modify: GlobalState => GlobalState)(implicit context: Context[_]): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => context.update(modify(context.global)))

  def setGlobal(set: => GlobalState)(implicit context: Context[_]): SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => context.update(set))

  val noop: SnabbdomFacade.Eventlistener =
    Snabbdom.event(_ => ())
}
