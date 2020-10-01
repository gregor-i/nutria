package nutria.frontend
package pages

import monocle.macros.Lenses
import nutria.frontend.Router.Location
import nutria.frontend.pages.common.{Body, Header}
import snabbdom._

import scala.concurrent.Future
import scala.util.{Failure, Success}

@Lenses
case class LoadingState(process: Future[PageState]) extends PageState

object LoadingPage extends Page[LoadingState] {
  def stateFromUrl = PartialFunction.empty

  def stateToUrl(state: State): Option[Location] = None

  override def render(implicit global: Global, local: Local) =
    Body()
      .child(Header())
      .child(
        Node("i.fa.fa-spinner.fa-pulse.has-text-primary")
          .styles(
            Seq(
              "position"   -> "absolute",
              "left"       -> "50%",
              "top"        -> "50%",
              "marginLeft" -> "-5rem",
              "fontSize"   -> "10rem"
            )
          )
      )
      .key(local.state.process.hashCode())
      .hook(
        "insert",
        Snabbdom.hook { _ =>
          local.state.process.onComplete {
            case Success(newState) => local.update(newState)
            case Failure(error)    => local.update(ErrorState.asyncLoadError(error))
          }
        }
      )
}
