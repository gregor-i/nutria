package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.ui.common.{FractalTile, _}
import nutria.frontend.{GalleryState, NutriaState, _}
import snabbdom._

object GalleryUi extends Page[GalleryState] {
  def render(implicit state: GalleryState, update: NutriaState => Unit) =
    Seq(
      common.Header(state, update),
      Node("div.container")
        .child(
          Node("div.fractal-tile-list")
            .children(
              state.publicFractals.map(renderFractalTile),
              dummyTiles
            )
        ),
      common.Footer()
    )

  def renderFractalTile(
      fractal: FractalEntityWithId
  )(implicit state: GalleryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .attr("title", fractal.entity.description)
      .child(
        FractalTile(FractalImage.firstImage(fractal.entity), Dimensions.thumbnailDimensions)
          .event(
            "click",
            Actions.exploreFractal(fractal)
          )
      )
      .child(
        Node("div.buttons.overlay-bottom-right.padding")
        /* Button.icon(Icons.upvote, Snabbdom.event { _ =>
           Button.icon(Icons.downvote, Snabbdom.event { _ => */
          .child(
            Button
              .icon(
                Icons.explore,
                Actions.exploreFractal(fractal)
              )
              .classes("is-outlined")
          )
          .child(
            Button
              .icon(Icons.edit, Actions.editFractal(fractal))
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

  val dummyTiles = Seq.fill(8)(dummyTile)
}
