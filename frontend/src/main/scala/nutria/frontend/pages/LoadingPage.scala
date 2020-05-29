package nutria.frontend.pages

import nutria.api.User
import nutria.frontend.Router.Location
import nutria.frontend.pages.common.{Body, Header}
import nutria.frontend.service.NutriaService
import nutria.frontend.{NutriaState, Page}
import snabbdom._

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class LoadingState(user: Option[User], loading: Future[NutriaState], navbarExpanded: Boolean = false) extends NutriaState {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object LoadingPage extends Page[LoadingState] {
  def stateFromUrl = PartialFunction.empty

  def stateToUrl(state: State): Option[Location] = None

  def render(implicit state: LoadingState, update: NutriaState => Unit) =
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
