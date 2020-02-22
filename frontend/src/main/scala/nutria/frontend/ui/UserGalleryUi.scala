package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.ui.common.{FractalTile, _}
import nutria.frontend.{Actions, Links, NutriaState, UserGalleryState}
import snabbdom.Node

object UserGalleryUi extends Page[UserGalleryState] {
  def render(implicit state: UserGalleryState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(
        Node("div.container")
          .child(
            Node("section.section")
              .child(Node("h1.title.is-1").text("User Fractal Gallery:"))
              .child(Node("h2.subtitle").text("user: " + state.aboutUser))
          )
          .child(
            Node("div.fractal-tile-list")
              .child(state.userFractals.map(renderFractalTile))
              .child(dummyTiles)
          )
      )
      .child(Footer())

  def renderFractalTile(
      fractal: FractalEntityWithId
  )(implicit state: UserGalleryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .child(
        Link(Links.detailsState(fractal, state.user))
          .child(
            FractalTile(FractalImage.firstImage(fractal.entity), Dimensions.thumbnailDimensions)
              .event("click", Actions.editFractal(fractal))
          )
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
