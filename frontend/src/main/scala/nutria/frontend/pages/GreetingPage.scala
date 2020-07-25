package nutria.frontend.pages

import nutria.api.User
import nutria.core.FractalImage
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Body, Button, ButtonList, CanvasHooks, Icons, Link, Modal}
import nutria.macros.StaticContent
import snabbdom.Node

case class GreetingState(user: Option[User], randomFractal: FractalImage) extends NutriaState

object GreetingPage extends Page[GreetingState] {
  override def stateFromUrl = {
    case (user, "/", _) => Links.greetingState(user).loading(user)
  }

  override def stateToUrl(state: GreetingState): Option[(Path, QueryParameter)] =
    Some("/" -> Map.empty)

  def render(implicit state: GreetingState, update: NutriaState => Unit) =
    Body()
      .child(renderCanvas)
      .child(content)

  private def content(implicit state: GreetingState, update: NutriaState => Unit) = {
    Modal(closeAction = Actions.exploreFractal())(
      Node("div.content").prop("innerHTML", StaticContent("frontend/src/main/html/greeting.html")),
      ButtonList(
        Link(DocumentationState.introduction)
          .classes("button", "is-link", "is-outlined")
          .child(Icons.icon(Icons.info))
          .child(Node("span").text("more information")),
        Link
          .async("/gallery", Links.galleryState(state.user))
          .classes("button", "is-primary")
          .child(Icons.icon(Icons.gallery))
          .child(Node("span").text("Start exploring!"))
      )
    )
  }

  private def renderCanvas(implicit state: GreetingState, update: ExplorerState => Unit): Node =
    Node("div.background")
      .child(
        Node("canvas").hooks(CanvasHooks(state.randomFractal))
      )
}
