package nutria.frontend

import nutria.frontend.util.Updatable

import scala.concurrent.Future

package object pages {
  implicit class EnrichFutureOfState(futState: Future[PageState]) {
    def loading(): LoadingState = LoadingState(futState)
  }

  def state[S <: PageState](implicit updatable: Updatable[S, _]): S = updatable.state
}
