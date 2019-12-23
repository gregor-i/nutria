package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend._
import common._
import nutria.frontend.ui.common.FractalTile
import nutria.frontend.{DetailsState, LibraryState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom._

object LibraryUi {
  def render(implicit state: LibraryState, update: NutriaState => Unit): VNode = {
    h(tag = "body",
      key = "gallery")(
      common.Header(state, update),
      h("div.container.is-fluid")(
        h("div.fractal-tile-list")(
          state.publicFractals.map(renderFractalTile),
          dummyTiles
        ),
      ),
      common.Footer()
    )
  }

  def renderFractalTile(fractal: FractalEntityWithId)
                       (implicit state: LibraryState, update: NutriaState => Unit): VNode =
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
        .toVNode

  private val dummyTile =
    h("article.dummy-tile")(
      h("canvas",
        attrs = Seq(
          "width" -> Dimensions.thumbnailDimensions.width.toString,
          "height" -> "0",
        )
      )()
    )

  val dummyTiles = Seq.fill(8)(dummyTile)
}
