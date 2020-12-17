package nutria.frontend.pages

import nutria.api.{Entity, FractalImageEntity}
import nutria.core.FractalImage
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import nutria.macros.StaticContent
import snabbdom.{Event, Node, Snabbdom}

import scala.util.chaining.scalaUtilChainingOps

case class GreetingState(randomFractal: FractalImageEntity) extends PageState

object GreetingPage extends Page[GreetingState] {
  override def stateFromUrl = {
    case (_, "/", _) => Links.greetingState().loading()
  }

  override def stateToUrl(state: GreetingState): Option[(Path, QueryParameter)] =
    Some("/" -> Map.empty)

  def render(implicit context: Context) =
    Body()
      .child(renderCanvas)
      .child(content)

  private def content(implicit context: Context) = {
    Modal(closeAction = _ => context.update(Links.explorerState(fractal = context.local.randomFractal)))(
      Node("div.content").prop("innerHTML", StaticContent("frontend/src/main/html/greeting.html")),
      ButtonList(
        Link(DocumentationState.introduction)
          .classes("button", "is-link", "is-outlined")
          .child(Icons.icon(Icons.info))
          .child(Node("span").text("more information")),
        Link
          .async("/gallery", Links.galleryState())
          .classes("button", "is-primary")
          .child(Icons.icon(Icons.gallery))
          .child(Node("span").text("Start exploring!"))
      )
    )
  }

  private def renderCanvas(implicit context: Context): Node =
    Node("div.background")
      .child(
        Node("canvas").pipe(CanvasHooks(context.local.randomFractal.value))
      )
}
