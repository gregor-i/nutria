package nutria.frontend.ui

import nutria.frontend.ui.common.{Button, CanvasHooks, Icons}
import nutria.frontend.{ExplorerState, GreetingState, LoadingState, NutriaState}
import snabbdom.{Node, Snabbdom}

object GreetingUi {
  def render(implicit state: GreetingState, update: NutriaState => Unit): Node =
    Node("body")
      .key("error")
      .child(renderCanvas)
      .child(content)

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
          .event("click", Snabbdom.event { _ =>
            update(ExplorerState(state.user, None, owned = false, state.randomFractal))
          }),
        Node("div.modal-content")
          .child(
            Node("div.box")
              .children(
                Node("div.content").prop("innerHTML", greetingContent),
                Node("div.buttons")
                  .child(
                    Button("Start exploring!", Icons.library, Snabbdom.event { _ =>
                      update(LoadingState(NutriaState.libraryState()))
                    }).classes("is-primary")
                  )
                  .classes("is-right")
              )
          )
      )
  }

  private def renderCanvas(implicit state: GreetingState, update: ExplorerState => Unit): Node =
    Node("div.full-size")
      .child(
        Node("canvas").hooks(CanvasHooks(state.randomFractal, resize = true))
      )
}
