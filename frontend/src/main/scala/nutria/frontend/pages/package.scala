package nutria.frontend

import nutria.api.User

import scala.concurrent.Future

package object pages {
  implicit class EnrichFutureOfState(futState: Future[NutriaState]) {
    def loading(user: Option[User]): LoadingState = LoadingState(user, futState)
  }
}
