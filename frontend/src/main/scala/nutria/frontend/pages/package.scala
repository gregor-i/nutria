package nutria.frontend

import nutria.frontend.util.Updatable

import scala.concurrent.Future

package object pages {
  implicit class EnrichFutureOfState(futState: Future[PageState]) {
    def loading(): LoadingState = LoadingState(futState)
  }

  implicit def updatablePageState[S <: PageState](implicit pageState: S, update: PageState => Unit): Updatable[S, PageState] =
    Updatable[S, PageState](pageState, update)
}
