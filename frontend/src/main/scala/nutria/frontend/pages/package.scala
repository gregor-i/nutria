package nutria.frontend

import scala.concurrent.Future

package object pages {
  implicit class EnrichFutureOfState(futState: Future[PageState]) {
    def loading(): LoadingState = LoadingState(futState)
  }
}
