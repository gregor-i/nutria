package nutria.frontend.ui

import nutria.frontend.ui.common.{Button, CanvasHooks, Icons, Link}
import nutria.frontend.{Actions, ExplorerState, GreetingState, Links, NutriaState}
import nutria.macros.StaticContent
import snabbdom.Node

object GreetingUi extends Page[GreetingState] {
  def render(implicit state: GreetingState, update: NutriaState => Unit) =
    Seq(renderCanvas, content)

  private def content(implicit state: GreetingState, update: NutriaState => Unit) = {
    Node("div.modal.is-active")
      .children(
        Node("div.modal-background")
          .style("opacity", "0.5")
          .event("click", Actions.exploreFractal(state.randomFractal)),
        Node("div.modal-content")
          .child(
            Node("div.box")
              .children(
                Node("div.content").prop("innerHTML", StaticContent("frontend/src/main/html/greeting.html")),
                Node("div.buttons")
                  .child(
                    Link
                      .async("/faq", Links.faqState())
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
