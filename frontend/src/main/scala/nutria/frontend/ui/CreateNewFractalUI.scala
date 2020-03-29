package nutria.frontend.ui

import nutria.frontend.ui.common.{Body, Header}
import nutria.frontend.{CreateNewFractalState, NutriaState}
import snabbdom.Node

object CreateNewFractalUI extends Page[CreateNewFractalState] {
  override def render(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
      .child(
        Node("div.container")
          .child(Node("section.section").child(Node("h1.title.is-1").text("Create new Fractal:")))
      )
}
