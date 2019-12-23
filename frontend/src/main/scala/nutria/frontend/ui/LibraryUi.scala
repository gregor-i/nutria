package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend._
import common._
import nutria.frontend.ui.common.FractalTile
import nutria.frontend.{DetailsState, LibraryState, NutriaState}
import snabbdom._

object LibraryUi {
  def render(implicit state: LibraryState, update: NutriaState => Unit): Node =
    Node("body")
    .key("gallery")
    .children(
      common.Header(state, update),
      Node("div.container.is-fluid")
      .child(
        Node("div.fractal-tile-list")
        .children(
          state.publicFractals.map(renderFractalTile),
          dummyTiles
        )
      ),
      common.Footer()
    )

  def renderFractalTile(fractal: FractalEntityWithId)
                       (implicit state: LibraryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .attr("title", fractal.entity.description)
    .child(
      FractalTile(FractalImage.firstImage(fractal.entity), Dimensions.thumbnailDimensions)
      .event("click", Snabbdom.event(_ =>
                      update(ExplorerState(user = state.user, fractalId = Some(fractal.id), owned = state.user.exists(_.id == fractal.owner), fractalImage = FractalImage.firstImage(fractal.entity)))
)    ))
        .child(
          Node("div.buttons.overlay-bottom-right.padding")
/*            .child(
              Button.icon(Icons.upvote, Snabbdom.event { _ =>
()
              })
                .classes("is-outlined")
            )
            .child(
              Button.icon(Icons.downvote, Snabbdom.event { _ =>
()
              })
                .classes("is-outlined")
            ) */
            .child(
              Button.icon(Icons.explore, Snabbdom.event { _ =>
                update(ExplorerState(user = state.user, fractalId = Some(fractal.id), owned = state.user.exists(_.id == fractal.owner), fractalImage = FractalImage.firstImage(fractal.entity)))
              })
              .classes("is-outlined")
            )
            .child(
              Button.icon(Icons.edit, Snabbdom.event { _ =>
                  update(
                        DetailsState(
                          user = state.user,
                          remoteFractal = fractal,
                          fractalToEdit = fractal)
                      )
              })
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
