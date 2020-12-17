package nutria.frontend.util

import nutria.frontend.{Context, GlobalState}
import snabbdom.Event

object SnabbdomUtil {
  def modify[S](modify: S => S)(implicit updatable: Updatable[S, S]): Event => Unit =
    _ => updatable.update(modify(updatable.state))

  def modifyGlobal(modify: GlobalState => GlobalState)(implicit context: Context[_]): Event => Unit =
    _ => context.update(modify(context.global))

  def setGlobal(set: => GlobalState)(implicit context: Context[_]): Event => Unit =
    _ => context.update(set)

  val noop: Event => Unit = _ => ()
}
