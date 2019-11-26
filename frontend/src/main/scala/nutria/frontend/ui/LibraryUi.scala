package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.ui.common.FractalImage
import nutria.frontend.{DetailsState, LibraryState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object LibraryUi {
  def render(implicit state: LibraryState, update: NutriaState => Unit): VNode = {
    h(tag = "body",
      key = "library")(
      common.Header("Nutria Fractal Library")(state, update),
      h("div.fractal-tile-list")(
        state.publicFractals.map(renderFractalTile),
        fiveDummyTiles
      ),
      common.Footer()
    )
  }

  def renderFractalTile(fractal: FractalEntityWithId)
                       (implicit state: LibraryState, update: NutriaState => Unit): VNode =
    h("article.fractal-tile",
      attrs = Seq("title" -> fractal.entity.description),
      events = Seq("click" -> Snabbdom.event(_ => update(
        DetailsState(
          user = state.user,
          remoteFractal = fractal,
          fractal = fractal.entity)
      )))
    )(
      FractalImage(fractal.entity, Dimensions.thumbnailDimensions)
    )


  val fiveDummyTiles = Seq.fill(5)(dummyTile)
  val dummyTile =
    h("article.dummy-tile")(
      h("canvas",
        attrs = Seq(
          "width" -> Dimensions.thumbnailDimensions.width.toString,
          "height" -> "0",
        )
      )()
    )
}
