package nutria.frontend

import nutria.api.User
import nutria.core._

trait NutriaState {
  def user: Option[User]
}
