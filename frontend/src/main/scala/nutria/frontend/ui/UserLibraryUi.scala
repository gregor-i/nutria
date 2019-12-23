package nutria.frontend.ui

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.ui.common.FractalTile
import nutria.frontend.{DetailsState, UserLibraryState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}
import nutria.frontend.ui.common._
import snabbdom.{Node, VNode}
import nutria.frontend.NutriaService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserLibraryUi {
  def render(implicit state: UserLibraryState, update: NutriaState => Unit): VNode = {
    h(tag = "body",
      key = "user-gallery")(
      common.Header(state, update),
      h("div.container.is-fluid")(
        h("div.fractal-tile-list")(
          state.userFractals.map(renderFractalTile),
          dummyTiles
        ),
      ),
      common.Footer()
    )
  }

  def renderFractalTile(fractal: FractalEntityWithId)
                       (implicit state: UserLibraryState, update: NutriaState => Unit): VNode =
    Node("article.fractal-tile.is-relative")
    .attr("title", fractal.entity.description)
    .child(
      FractalTile(FractalImage.firstImage(fractal.entity), Dimensions.thumbnailDimensions)
      .event("click", Snabbdom.event(_ => update(
              DetailsState(
                user = state.user,
                remoteFractal = fractal,
                fractalToEdit = fractal)
            )))
      .toVNode
    )
    .child(
      Node("div.buttons.overlay-top-right.padding")
        .child(
          Button.icon(if(fractal.entity.published) Icons.unpublish else Icons.publish, Snabbdom.event { _ =>
            (for{
              _ <- NutriaService.updateUserFractal(FractalEntityWithId.entity.composeLens(FractalEntity.published).modify(!_).apply(fractal))
              reloaded <- reload(state)
            } yield reloaded)
                .foreach(update)
          })
            .classes("is-outlined")
        )
        .child(
          Button.icon(Icons.delete, Snabbdom.event { _ =>
          // todo: add alert or dialog
            (for{
              _ <- NutriaService.deleteUserFractal(state.aboutUser, fractal.id)
              reloaded <- reload(state)
            } yield reloaded)
                .foreach(update)
          })
            .classes("is-outlined")
        )
    )
    .toVNode

  private def reload(implicit state: UserLibraryState): Future[UserLibraryState] =
    for {
        all <- NutriaService.loadUserFractals(state.aboutUser)
    } yield state.copy(userFractals = all)


  private val dummyTile =
    h("article.dummy-tile")(
      h("canvas",
        attrs = Seq(
          "width" -> Dimensions.thumbnailDimensions.width.toString,
          "height" -> "0",
        )
      )()
    )

  val dummyTiles = Seq.fill(8)(dummyTile)
}
