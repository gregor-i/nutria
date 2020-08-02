package nutria.frontend.toasts

import nutria.frontend.toasts

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object Syntax {

  def withSuccessToast[A](progressText: String, onComplete: A => String)(progress: Future[A]): Future[A] = {
    Toasts.futureToast[A](
      progressText,
      progress, {
        case Success(value) => toasts.Success -> onComplete(value)
        case Failure(ex)    => toasts.Danger  -> "The last action was not successful. Please retry the action or reload the page."
      }
    )
    progress
  }

  def withSuccessToast[A](progressText: String, text: String)(progress: Future[A]): Future[A] =
    withSuccessToast(progressText, (_: A) => text)(progress)

  def withWarningToast[A](progressText: String, onComplete: A => String)(progress: Future[A]): Future[A] = {
    Toasts.futureToast[A](
      progressText,
      progress, {
        case Success(value) => toasts.Warning -> onComplete(value)
        case Failure(ex)    => toasts.Danger  -> "The last action was not successful. Please retry the action or reload the page."
      }
    )
    progress
  }

  def withWarningToast[A](progressText: String, text: String)(progress: Future[A]): Future[A] =
    withWarningToast(progressText, (_: A) => text)(progress)

  implicit class EnrichFuture[A](val progress: Future[A]) extends AnyVal {
    def withToast(progressText: String, onComplete: Try[A] => (ToastType, String)): Future[A] = {
      Toasts.futureToast(progressText, progress, onComplete)
      progress
    }

    def withSuccessToast(progressText: String, onComplete: A => String): Future[A] = Syntax.withSuccessToast(progressText, onComplete)(progress)
    def withSuccessToast(progressText: String, onComplete: String): Future[A]      = Syntax.withSuccessToast(progressText, onComplete)(progress)
    def withWarningToast(progressText: String, onComplete: A => String): Future[A] = Syntax.withWarningToast(progressText, onComplete)(progress)
    def withWarningToast(progressText: String, onComplete: String): Future[A]      = Syntax.withWarningToast(progressText, onComplete)(progress)
  }
}
