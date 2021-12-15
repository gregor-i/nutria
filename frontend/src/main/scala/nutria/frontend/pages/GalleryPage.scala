package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.{FractalImageEntity, WithId}
import nutria.core.Dimensions
import nutria.frontend._
import nutria.frontend.pages.common.{FractalTile, _}
import snabbdom._
import snabbdom.components.Icon

import scala.util.chaining._

@Lenses
case class GalleryState(
    publicFractals: Seq[WithId[FractalImageEntity]],
    page: Int
) extends PageState

object GalleryPage extends Page[GalleryState] {
  override def stateFromUrl = {
    case (user, "/gallery", query) =>
      val page = query.get("page").flatMap(_.toIntOption).getOrElse(1)
      Links.galleryState(page).loading()
  }

  override def stateToUrl(state: GalleryPage.State): Option[Router.Location] =
    Some("/gallery" -> Map("page" -> state.page.toString))

  def render(implicit context: Context) =
    Body()
      .child(Header())
      .child(
        Link
          .async("/new-fractal", CreateNewFractalState.load(context.global))
          .pipe(Header.fab)
          .child(Icon(Icons.plus))
      )
      .child(
        "div.container"
          .child(
            "section.section"
              .child("h1.title.is-1".text("Fractal Gallery:"))
          )
          .child(
            "div.fractal-tile-list"
              .child(Pagination.page(GalleryState.publicFractals, GalleryState.page).map(renderFractalTile(_)))
              .child(dummyTiles)
          )
          .child(Pagination.links(GalleryState.publicFractals, GalleryState.page))
      )
      .child(Footer())

  def renderFractalTile(fractal: WithId[FractalImageEntity])(implicit context: Context): Node =
    "article.fractal-tile.is-relative"
      .child(
        Link(Links.explorerState(fractal))
          .child(
            FractalTile(fractal.entity.value, Dimensions.thumbnail)
          )
      )
      .child(
        "div.buttons.overlay-bottom-right.padding"
          .child(
            Link(Links.explorerState(fractal))
              .classes("button", "is-outlined", "is-rounded")
              .child(Icon(Icons.explore))
          )
          .child(
            Link(Links.explorerStateWithModal(fractal))
              .classes("button", "is-outlined", "is-rounded")
              .child(Icon(Icons.edit))
          )
      )

  private val dummyTile =
    "article.dummy-tile"
      .child(
        "canvas"
          .attr("width", Dimensions.thumbnail.width.toString)
          .attr("height", "0")
      )

  val dummyTiles = Seq.fill(8)(dummyTile)
}
