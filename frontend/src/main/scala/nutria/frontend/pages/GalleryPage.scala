package nutria.frontend.pages

import nutria.api.{FractalImageEntity, User, VoteStatistic, WithId}
import nutria.core.Dimensions
import nutria.frontend._
import nutria.frontend.pages.common.{FractalTile, _}
import snabbdom._

import scala.util.chaining._

case class GalleryState(
    user: Option[User],
    publicFractals: Vector[WithId[FractalImageEntity]],
    votes: Map[String, VoteStatistic],
    navbarExpanded: Boolean = false
) extends NutriaState {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object GalleryPage extends Page[GalleryState] {

  override def stateFromUrl = {
    case (user, "/gallery", _) =>
      Links.galleryState(user).loading(user)
  }

  override def stateToUrl(state: GalleryPage.State): Option[Router.Location] =
    Some("/gallery" -> Map.empty)

  def render(implicit state: GalleryState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(
        Link
          .async("/new-fractal", CreateNewFractalState.load(state.user))
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
              .child(state.publicFractals.map(renderFractalTile(_)))
              .child(dummyTiles)
          )
      )
      .child(Footer())

  def renderFractalTile(
      fractal: WithId[FractalImageEntity]
  )(implicit state: GalleryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .child(
        Link(Links.explorerState(fractal, state.user))
          .child(
            FractalTile(fractal.entity.value, Dimensions.thumbnail)
          )
      )
      .child(
        Node("div.buttons.overlay-bottom-right.padding")
          .child(
            Link(Links.explorerState(fractal, state.user))
              .classes("button", "is-outlined", "is-rounded")
              .child(Icons.icon(Icons.explore))
          )
          .child(
            Link(Links.detailsState(fractal, state.user))
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
