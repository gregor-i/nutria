package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.ui.common.{FractalTile, _}
import nutria.frontend.{GalleryState, NutriaState, _}
import snabbdom._

import scala.util.chaining._

object GalleryUi extends Page[GalleryState] {
  def render(implicit state: GalleryState, update: NutriaState => Unit) =
    Body()
      .child(Header())
      .child(
        Link(CreateNewFractalState(user = state.user))
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
              .children(
                state.publicFractals
                  .map(fractal => renderFractalTile(fractal, state.votes.getOrElse(fractal.id, VoteStatistic.empty))),
                dummyTiles
              )
          )
      )
      .child(Footer())

  def renderFractalTile(
      fractal: FractalEntityWithId,
      voteStatistic: VoteStatistic
  )(implicit state: GalleryState, update: NutriaState => Unit): Node =
    Node("article.fractal-tile.is-relative")
      .child(
        Link(Links.explorerState(fractal, state.user))
          .child(
            FractalTile(FractalImage.firstImage(fractal.entity), Dimensions.thumbnail)
          )
      )
      .child(
        Node("div.buttons.overlay-bottom-right.padding")
          .child(
            if (!voteStatistic.yourVerdict.contains(UpVote))
              Button.icon(Icons.upvote, Actions.vote(fractal.id, UpVote))
            else
              Button.icon(Icons.upvote, Actions.removeVote(fractal.id)).classes("is-primary")
          )
          .child(
            if (!voteStatistic.yourVerdict.contains(DownVote))
              Button.icon(Icons.downvote, Actions.vote(fractal.id, DownVote))
            else
              Button.icon(Icons.downvote, Actions.removeVote(fractal.id)).classes("is-primary")
          )
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
