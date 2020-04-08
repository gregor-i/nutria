package nutria.frontend

import nutria.core._

trait NutriaState {
  def user: Option[User]
  def navbarExpanded: Boolean
  def setNavbarExtended(boolean: Boolean): NutriaState
}

trait NoUser {
  _: NutriaState =>
  def user: None.type = None
}
