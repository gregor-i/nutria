package nutria.frontend.pages

import nutria.frontend.pages.common.{Body, Header}
import nutria.frontend.{NoRouting, NoUser, NutriaState, Page}
import snabbdom._

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class LoadingState(loading: Future[NutriaState], navbarExpanded: Boolean = false) extends NutriaState with NoUser {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object LoadingPage extends Page[LoadingState] with NoRouting[LoadingState] {
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
                ErrorState(s"unexpected problem while initializing app: ${exception.getMessage}")
              )
          }
        }
      )
}
