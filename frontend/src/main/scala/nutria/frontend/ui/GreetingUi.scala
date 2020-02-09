package nutria.frontend.ui

import nutria.frontend.ui.common.{Button, CanvasHooks, Icons, Link}
import nutria.frontend.{Actions, ExplorerState, GreetingState, Links, NutriaState}
import snabbdom.Node

object GreetingUi extends Page[GreetingState] {
  def render(implicit state: GreetingState, update: NutriaState => Unit) =
    Seq(renderCanvas, content)

  private val greetingContent =
    """<h1>Nutria - Fractal Explorer</h1>
      |<h2>What is a fractal?</h2>
      |<p>
      | Giving an accurate definition of a fractal is not really easy, but for the purpose of this projects it enough to say that a fractal is an image with infinite depth.
      | That means you can zoom into it and will always continue to unfold its structure.
      |</p>
      |
      |<h2>What is Nutria?</h2>
      |<p>Nutria is basically a gallery of fractals and it contains a convenient tool to explore such fractals.</p>
      |<p>Fractals usually have a lot of parameters. Nutria allows you to try out new configurations for these parameters which might yield completely new images.</p>
    """.stripMargin

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
                Node("div.content").prop("innerHTML", greetingContent),
                Node("div.buttons")
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
