package nutria.frontend.pages

import monocle.macros.Lenses
import nutria.api.{FractalImageEntity, User, VoteStatistic, WithId}
import nutria.core.Dimensions
import nutria.frontend._
import nutria.frontend.pages.common.{FractalTile, _}
import snabbdom._

import scala.util.chaining._

@Lenses
case class GalleryState(
    publicFractals: Seq[WithId[FractalImageEntity]],
    page: Int,
    navbarExpanded: Boolean = false
) extends PageState

object GalleryPage extends Page[GalleryState] {

  override def stateFromUrl = {
    case (user, "/gallery", query) =>
      val page = query.get("page").flatMap(_.toIntOption).getOrElse(1)
      Links.galleryState(page).loading()
  }

  override def stateToUrl(state: GalleryPage.State): Option[Router.Location] =
    Some("/gallery" -> Map("page" -> state.page.toString))

  def render(implicit globalState: GlobalState, state: GalleryState, update: PageState => Unit) =
    Body()
      .child(Header(GalleryState.navbarExpanded))
      .child(
        Link
          .async("/new-fractal", CreateNewFractalState.load(globalState))
          .pipe(Header.fab)
          .child(Icons.icon(Icons.plus))
      )
      .child(
        Node("div.container")
          .child(
            Node("section.section")
              .child(Node("h1.title.is-1").text("Fractal Gallery:"))
          )
          .child(
            Node("div.fractal-tile-list")
              .child(Pagination.page(GalleryState.publicFractals, GalleryState.page).map(renderFractalTile(_)))
              .child(dummyTiles)
          )
          .child(Pagination.links(GalleryState.publicFractals, GalleryState.page))
      )
      .child(Footer())

  def renderFractalTile(
      fractal: WithId[FractalImageEntity]
  )(implicit globalState: GlobalState, state: GalleryState, update: PageState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .child(
        Link(Links.explorerState(fractal))
          .child(
            FractalTile(fractal.entity.value, Dimensions.thumbnail)
          )
      )
      .child(
        Node("div.buttons.overlay-bottom-right.padding")
          .child(
            Link(Links.explorerState(fractal))
              .classes("button", "is-outlined", "is-rounded")
              .child(Icons.icon(Icons.explore))
          )
          .child(
            Link(Links.detailsState(fractal))
              .classes("button", "is-outlined", "is-rounded")
              .child(Icons.icon(Icons.edit))
          )
      )

  private val dummyTile =
    Node("article.dummy-tile")
      .child(
        Node("canvas")
          .attr("width", Dimensions.thumbnail.width.toString)
          .attr("height", "0")
      )

  val dummyTiles = Seq.fill(8)(dummyTile)
}
