package nutria.frontend
package pages

import monocle.macros.Lenses
import nutria.api.{FractalImageEntity, WithId}
import nutria.core.Dimensions
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{FractalTile, _}
import snabbdom.Node
import snabbdom.components.{Button, Icon}

import scala.util.chaining._

@Lenses
case class UserGalleryState(
    aboutUser: String,
    userFractals: Seq[WithId[FractalImageEntity]],
    page: Int = 1
) extends PageState

object UserGalleryPage extends Page[UserGalleryState] {
  override def stateToUrl(state: UserGalleryPage.State): Option[(Path, QueryParameter)] =
    Some(s"/user/${state.aboutUser}/gallery" -> Map("page" -> state.page.toString))

  override def stateFromUrl = {
    case (user, s"/user/${userId}/gallery", query) =>
      val page = query.get("page").flatMap(_.toIntOption).getOrElse(1)
      Links.userGalleryState(userId, page = page).loading()
  }

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
              .child("h1.title.is-1".text("User Fractal Gallery:"))
              .child("h2.subtitle".text("Here are all your fractals listed"))
          )
          .child(
            "div.fractal-tile-list"
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

  def renderFractalTile(fractal: WithId[FractalImageEntity])(implicit context: Context): Node =
    "article.fractal-tile.is-relative"
      .child(
        Link(Links.explorerState(fractal))
          .child(FractalTile(fractal.entity.value, Dimensions.thumbnail))
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
    "article.dummy-tile"
      .child(
        "canvas"
          .attr("width", Dimensions.thumbnail.width.toString)
          .attr("height", "0")
      )

  val dummyTiles: Seq[Node] = Seq.fill(8)(dummyTile)
}
