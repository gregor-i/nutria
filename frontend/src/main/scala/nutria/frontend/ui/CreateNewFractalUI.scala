package nutria.frontend.ui

import eu.timepit.refined.refineMV
import nutria.core.viewport.Dimensions
import nutria.core.{DivergingSeries, FractalImage, NewtonIteration}
import nutria.frontend.ui.common.{Body, FractalTile, Header}
import nutria.frontend.{CreateNewFractalState, NutriaState}
import snabbdom.Node

object CreateNewFractalUI extends Page[CreateNewFractalState] {
  override def render(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
      .child(
        Node("div.container")
          .child(Node("section.section").child(Node("h1.title.is-1").text("Create new Fractal:")))
          .child(selectSeries())
      )

  private def selectSeries()(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Node("section.section")
      .child(Node("h2.title.is-2").text("Step 1: Select the fractal type"))
      .child(
        Node("div.fractal-list")
          .child(
            FractalTile(
              fractalImage = FractalImage(
                program = DivergingSeries.default,
                antiAliase = refineMV(2)
              ),
              dimensions = Dimensions.thumbnailDimensions
            )
          )
          .child(
            FractalTile(
              fractalImage = FractalImage(
                program = NewtonIteration.default,
                antiAliase = refineMV(2)
              ),
              dimensions = Dimensions.thumbnailDimensions
            )
          )
      )
}
