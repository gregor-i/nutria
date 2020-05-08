package nutria.frontend.pages

import nutria.frontend._
import nutria.frontend.pages.common.{Body, Header}
import snabbdom._

case class ErrorState(message: String, navbarExpanded: Boolean = false) extends NutriaState with NoUser {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object ErrorPage extends Page[ErrorState] with NoRouting[ErrorState] {
  def render(implicit state: ErrorState, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
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