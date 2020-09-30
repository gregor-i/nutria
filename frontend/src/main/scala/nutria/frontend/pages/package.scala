package nutria.frontend

import nutria.frontend.util.Updatable

import scala.concurrent.Future

package object pages {
  implicit class EnrichFutureOfState(futState: Future[PageState]) {
    def loading(): LoadingState = LoadingState(futState)
  }

  implicit def updatablePageState[S <: PageState](implicit pageState: S, update: PageState => Unit): Updatable[S, PageState] =
    Updatable[S, PageState](pageState, update)

  def state[S <: PageState](implicit updatable: Updatable[S, _]): S = updatable.state
//  implicit def update(implicit updatable: Updatable[_, PageState]): PageState => Unit = updatable.update
}
