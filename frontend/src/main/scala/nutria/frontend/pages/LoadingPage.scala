package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.Location
import nutria.frontend.pages.common.{Body, Header}
import nutria.frontend.{NutriaState, Page}
import snabbdom._

import scala.concurrent.Future
import scala.util.{Failure, Success}

@Lenses
case class LoadingState(user: Option[User], loading: Future[NutriaState], navbarExpanded: Boolean = false) extends NutriaState

object LoadingPage extends Page[LoadingState] {
  def stateFromUrl = PartialFunction.empty

  def stateToUrl(state: State): Option[Location] = None

  def render(implicit state: LoadingState, update: NutriaState => Unit) =
    Body()
      .child(Header(LoadingState.navbarExpanded))
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
      .hook(
        "insert",
        Snabbdom.hook { _ =>
          state.loading.onComplete {
            case Success(newState) => update(newState)
            case Failure(exception) =>
              update(
                ErrorState(state.user, s"unexpected problem while initializing app: ${exception.getMessage}")
              )
          }
        }
      )
}
