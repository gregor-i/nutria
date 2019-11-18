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
      h("h2.title")("Public Fractals:"),
      h("div.lobby-tile-list")(
        state.publicFractals.map(renderProgramTile),
        Seq.fill(5)(dummyTile)
      ),
      common.Footer()
    )
  }

  def renderProgramTile(fractal: FractalEntityWithId)
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


  val dummyTile =
    h("article.dummy-tile")(
      h("div")(
        h("canvas",
          attrs = Seq(
            "width" -> Dimensions.thumbnailDimensions.width.toString,
            "height" -> "0",
          ),
          styles = Seq(
            "display" -> "block",
          )
        )()
      )
    )
}
