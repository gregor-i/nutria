package nutria.frontend

import monocle.macros.Lenses
import nutria.api.User

@Lenses
case class GlobalState(user: Option[User], navbarExpanded: Boolean)

object GlobalState {
  val initial: GlobalState = GlobalState(user = None, navbarExpanded = false)
}
