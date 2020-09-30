package nutria.frontend

import nutria.api.User

case class GlobalState(user: Option[User])

object GlobalState {
  val initial: GlobalState = GlobalState(None)
}
