package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.Router.Location
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Header}
import snabbdom._

@Lenses
case class ErrorState(message: String, navbarExpanded: Boolean = false) extends PageState

object ErrorState {
  val unauthorized = ErrorState("You are not logged in")
}

object ErrorPage extends Page[ErrorState] {
  def stateFromUrl = PartialFunction.empty

  def stateToUrl(state: State): Option[Location] = None

  def render(implicit globalState: GlobalState, state: ErrorState, update: PageState => Unit): Node =
    Body()
      .child(Header(ErrorState.navbarExpanded))
      .child(
        Node("div.section")
          .child(
            Node("article.message.is-danger")
              .child(
                Node("div.message-body")
                  .child(Node("div.title").text("An unexpected error occured."))
                  .child(Node("div.subtitle").text(state.message))
                  .child(Node("a").attr("href", "/").text("return to landing page"))
              )
          )
      )
}
