package nutria.frontend.ui

import nutria.frontend.ui.common.{Button, ButtonGroup, CanvasHooks, Icons}
import nutria.frontend.{ExplorerState, GreetingState, LoadingState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object GreetingUi {
  def render(implicit state: GreetingState, update: NutriaState => Unit): VNode = {
    h("body")(
      renderCanvas,
      content
    )
  }

  private val greetingContent =
    """<h1>Nutria - Fractal Explorer</h1>
      |<h2>What is a fractal?</h2>
      |<p>
      | Giving an accurate definition of a fractal is not really easy, but for the purpose of this projects it enough to say that a fractal is an image with infinite depth.
      | That means you can zoom into it and will always continue to unfold its structure.
      |</p>
      |
      |<h2>What is Nutria?</h2>
      |<p>Nutria is basically a library of fractals and it contains a convenient tool to explore such fractals.</p>
      |<p>Fractals usually have a lot of parameters. Nutria allows you to try out new configurations for these parameters which will might yield completely new images.</p>
    """.stripMargin

  private def content(implicit state: GreetingState, update: NutriaState => Unit) = {
    h("div.modal.is-active")(
      h("div.modal-background",
        styles = Seq("opacity" -> "0.5"),
        events = Seq("click" ->
          Snabbdom.event { _ =>
            update(ExplorerState(state.user, None, state.randomFractal))
          }))(),
      h("div.modal-content")(
        h("div.box")(
          h("div.content", props = Seq("innerHTML" -> greetingContent))(),
          ButtonGroup(
            Button(
              "Start exploring!",
              Icons.library,
              Snabbdom.event { _ =>
                update(LoadingState(NutriaState.libraryState()))
              })
              .toVNode
          )
            .classes("is-right")
            .toVNode
        )
      )
    )
  }

  private def renderCanvas(implicit state: GreetingState, update: ExplorerState => Unit): VNode =
    h("div.full-size")(
      h("canvas",
        hooks = CanvasHooks(state.randomFractal, resize = true)
      )()
    )
}
