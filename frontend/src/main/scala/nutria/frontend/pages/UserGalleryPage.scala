package nutria.frontend
package pages

import monocle.macros.Lenses
import nutria.api.{FractalImageEntity, WithId}
import nutria.core.Dimensions
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{FractalTile, _}
import nutria.frontend.util.Updatable
import snabbdom.Node

import scala.util.chaining._

@Lenses
case class UserGalleryState(
    aboutUser: String,
    userFractals: Seq[WithId[FractalImageEntity]],
    page: Int
) extends PageState

object UserGalleryPage extends Page[UserGalleryState] {

  override def stateToUrl(state: UserGalleryPage.State): Option[(Path, QueryParameter)] =
    Some(s"/user/${state.aboutUser}/gallery" -> Map("page" -> state.page.toString))

  override def stateFromUrl = {
    case (user, s"/user/${userId}/gallery", query) =>
      val page = query.get("page").flatMap(_.toIntOption).getOrElse(1)
      Links.userGalleryState(userId, page = page).loading()

  }

  override def render(implicit global: Global, local: Local) =
    Body()
      .child(Header())
      .child(
        Link
          .async("/new-fractal", CreateNewFractalState.load(global.state))
          .pipe(Header.fab)
          .child(Icons.icon(Icons.plus))
      )
      .child(
        Node("div.container")
          .child(
            Node("section.section")
              .child(Node("h1.title.is-1").text("User Fractal Gallery:"))
              .child(Node("h2.subtitle").text("Here are all your fractals listed"))
          )
          .child(
            Node("div.fractal-tile-list")
              .child(
                Pagination
                  .page(UserGalleryState.userFractals, UserGalleryState.page)
                  .map(renderFractalTile)
              )
              .child(dummyTiles)
          )
          .child(Pagination.links(UserGalleryState.userFractals, UserGalleryState.page))
      )
      .child(Footer())

  def renderFractalTile(
      fractal: WithId[FractalImageEntity]
  )(implicit global: Global, local: Local): Node =
    Node("article.fractal-tile.is-relative")
      .child(
        Link(Links.detailsState(fractal))
          .child(
            FractalTile(fractal.entity.value, Dimensions.thumbnail)
              .event("click", Actions.editFractal(fractal))
          )
      )
      .child(
        Node("div.buttons.overlay-bottom-right.padding")
          .child(
            Button
              .icon(
                if (fractal.entity.published) Icons.unpublish else Icons.publish,
                Actions.togglePublishedImage(fractal)
              )
              .classes("is-outlined")
          )
          .child(
            Button
              .icon(
                Icons.delete,
                Actions.deleteFractalFromUserGallery(fractal.id)
              )
              .classes("is-outlined")
          )
      )

  private val dummyTile =
    Node("article.dummy-tile")
      .child(
        Node("canvas")
          .attr("width", Dimensions.thumbnail.width.toString)
          .attr("height", "0")
      )

  val dummyTiles: Seq[Node] = Seq.fill(8)(dummyTile)
}
