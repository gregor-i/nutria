package nutria.frontend.pages

import nutria.api.{FractalImageEntity, User, WithId}
import nutria.core.Dimensions
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{FractalTile, _}
import nutria.frontend.{Actions, Links, NutriaState, Page}
import snabbdom.Node

case class UserGalleryState(
    user: Option[User],
    aboutUser: String,
    userFractals: Vector[WithId[FractalImageEntity]],
    navbarExpanded: Boolean = false
) extends NutriaState {
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object UserGalleryPage extends Page[UserGalleryState] {

  override def stateToUrl(state: UserGalleryPage.State): Option[(Path, QueryParameter)] =
    Some(s"/user/${state.aboutUser}/gallery" -> Map.empty)

  override def stateFromUrl = {
    case (user, s"/user/${userId}/gallery", _) =>
      Links.userGalleryState(user, userId).loading(user)

  }

  def render(implicit state: UserGalleryState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      //      .child(
      //        Link(CreateNewFractalState(user = state.user))
      //          .pipe(Header.fab)
      //          .child(Icons.icon(Icons.plus))
      //      )
      .child(
        Node("div.container")
          .child(
            Node("section.section")
              .child(Node("h1.title.is-1").text("User Fractal Gallery:"))
              .child(Node("h2.subtitle").text("Here are all your fractals listed"))
          )
          .child(
            Node("div.fractal-tile-list")
              .child(state.userFractals.map(renderFractalTile))
              .child(dummyTiles)
          )
      )
      .child(Footer())

  def renderFractalTile(
      fractal: WithId[FractalImageEntity]
  )(implicit state: UserGalleryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .child(
        Link(Links.detailsState(fractal, state.user))
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
          .attr("width", Dimensions.thumbnail.width.toString)
          .attr("height", "0")
      )

  val dummyTiles: Seq[Node] = Seq.fill(8)(dummyTile)
}
