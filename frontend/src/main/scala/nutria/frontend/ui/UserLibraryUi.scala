package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.ui.common.{FractalTile, _}
import nutria.frontend.{Actions, DetailsState, NutriaService, NutriaState, UserLibraryState}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserLibraryUi {
  def render(implicit state: UserLibraryState, update: NutriaState => Unit): Node =
    Node("body")
      .key("user-gallery")
      .child(common.Header(state, update))
      .child(
        Node("div.container.is-fluid")
          .child(
            Node("div.fractal-tile-list")
              .child(state.userFractals.map(renderFractalTile))
              .child(dummyTiles)
          )
      )
      .child(common.Footer())

  def renderFractalTile(
      fractal: FractalEntityWithId
  )(implicit state: UserLibraryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .attr("title", fractal.entity.description)
      .child(
        FractalTile(FractalImage.firstImage(fractal.entity), Dimensions.thumbnailDimensions)
          .event("click", Actions.editFractal(fractal))
      )
      .child(
        Node("div.buttons.overlay-bottom-right.padding")
          .child(
            Button
              .icon(
                if (fractal.entity.published) Icons.unpublish else Icons.publish,
                Actions.togglePublished(fractal)
              )
              .classes("is-outlined")
          )
          .child(
            Button
              .icon(
                Icons.delete,
                Actions.deleteFractal(fractal.id)
              )
              .classes("is-outlined")
          )
      )

  private val dummyTile =
    Node("article.dummy-tile")
      .child(
        Node("canvas")
          .attr("width", Dimensions.thumbnailDimensions.width.toString)
          .attr("height", "0")
      )

  val dummyTiles: Seq[Node] = Seq.fill(8)(dummyTile)
}
