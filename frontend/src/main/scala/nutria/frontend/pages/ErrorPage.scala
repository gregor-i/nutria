package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.frontend.Router.Location
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Header}
import snabbdom._

@Lenses
case class ErrorState(message: String) extends PageState

object ErrorState {
  val unauthorized                     = ErrorState("You are not logged in")
  def asyncLoadError(error: Throwable) = ErrorState(s"unexpected problem while initializing app: ${error.getMessage}")
}

object ErrorPage extends Page[ErrorState] {
  def stateFromUrl = PartialFunction.empty

  def stateToUrl(state: State): Option[Location] = None

  def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(
        "div.section"
          .child(
            "article.message.is-danger"
              .child(
                "div.message-body"
                  .child("div.title".text("An unexpected error occured."))
                  .child("div.subtitle".text(context.local.message))
                  .child("a".attr("href", "/").text("return to landing page"))
              )
          )
      )
}
