package nutria.frontend.pages

import nutria.core.FractalImage
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Body, CanvasHooks, Icons, Link}
import nutria.macros.StaticContent
import snabbdom.Node

case class GreetingState(randomFractal: FractalImage, navbarExpanded: Boolean = false) extends NutriaState with NoUser {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object GreetingPage extends Page[GreetingState] {
  override def stateFromUrl: PartialFunction[(Path, QueryParameter), NutriaState] = {
    case ("/", _) => LoadingState(Links.greetingState())
  }

  override def stateToUrl(state: GreetingState): Option[(Path, QueryParameter)] =
    Some("/" -> Map.empty)

  def render(implicit state: GreetingState, update: NutriaState => Unit) =
    Body()
      .child(renderCanvas)
      .child(content)

  private def content(implicit state: GreetingState, update: NutriaState => Unit) = {
    Node("div.modal.is-active")
      .children(
        Node("div.modal-background")
          .event("click", Actions.exploreFractal(state.randomFractal)),
        Node("div.modal-content")
          .child(
            Node("div.box")
              .children(
                Node("div.content").prop("innerHTML", StaticContent("frontend/src/main/html/greeting.html")),
                Node("div.buttons")
                  .child(
                    Link(Actions.gotoFAQ())
                      .classes("button", "is-link", "is-outlined")
                      .child(Icons.icon(Icons.info))
                      .child(Node("span").text("more information"))
                  )
                  .child(
                    Link
                      .async("/gallery", Links.galleryState())
                      .classes("button", "is-primary")
                      .child(Icons.icon(Icons.gallery))
                      .child(Node("span").text("Start exploring!"))
                  )
                  .classes("is-right")
              )
          )
      )
  }

  private def renderCanvas(implicit state: GreetingState, update: ExplorerState => Unit): Node =
    Node("canvas.background").hooks(CanvasHooks(state.randomFractal, resize = true))
}