package nutria.frontend

import nutria.frontend.util.Updatable

import scala.concurrent.Future

package object pages {

  implicit class EnrichFutureOfState(futState: Future[PageState]) {
    def loading(): LoadingState = LoadingState(futState)
  }

  implicit def localUpdatable[S <: PageState](implicit context: Context[S]): Updatable[S, PageState] =
    context.localUpdatable
}
