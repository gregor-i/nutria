package nutria.frontend.library

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.common.{Buttons, FractalImage, Images}
import nutria.frontend.util.LenseUtils
import nutria.frontend.{LibraryState, NutriaService, NutriaState, common}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}
import scala.concurrent.ExecutionContext.Implicits.global

object LibraryUi {
  def render(implicit state: LibraryState, update: NutriaState => Unit): VNode = {
    h(tag = "body",
      key = "library")(
      common.Header("Nutria Fractal Library")(state, update),
      h("h2.title")("Your Fractals:"),
      h("div.lobby-tile-list")(
        state.personalFractals.map(renderProgramTile),
        Seq.fill(5)(dummyTile)
      ),
      h("h2.title")("Public Fractals:"),
      h("div.lobby-tile-list")(
        state.publicFractals.map(renderProgramTile),
        Seq.fill(5)(dummyTile)
      ),
      renderPopup().toSeq,
      common.Footer()
    )
  }

  def renderPopup()
                 (implicit state: LibraryState, update: NutriaState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      h("div.modal.is-active")(
        h("div.modal-background",
          events = Seq(
            "click" -> Snabbdom.event(_ => update(state.copy(edit = None)))
          ),
        )(),
        common.RenderEditFractalEntity(
          fractal = fractal.entity,
          currentTab = state.tab,
          lens = LenseUtils.lookedUp(fractal.entity, LibraryState.editOptional.composeLens(FractalEntityWithId.entity).asSetter),
          lensTab = LibraryState.tab,
          footer = popupActions(fractal)
        )
      )
    }

  private def popupActions(fractal: FractalEntityWithId)
                          (implicit state: LibraryState, update: NutriaState => Unit) =
  if (state.user.map(_.id).contains(fractal.owner)) {
    Buttons.group(
      Buttons("Apply Changes", Images.upload, Snabbdom.event { _ =>
        (for {
          _ <- NutriaService.updateUserFractal(fractal)
          personalFractals <- NutriaService.loadUserFractals(state.user.get.id)
        } yield state.copy(personalFractals = personalFractals))
          .foreach(update)
      }, `class` = ".is-primary"),
      Buttons("Delete", Images.delete, Snabbdom.event { _ =>
        (for {
          _ <- NutriaService.deleteUserFractal(state.user.get.id, fractal.id)
          personalFractals <- NutriaService.loadUserFractals(state.user.get.id)
        } yield state.copy(personalFractals = personalFractals, edit = None))
          .foreach(update)
      }, `class` = ".is-danger"),
      Buttons("Close", Images.cancel, Snabbdom.event { _ =>
        update(state.copy(edit = None))
      })
    )
  } else {
    Buttons.group(
      Buttons("Fork", Images.copy, Snabbdom.event(_ => println("todo: fork!")),
        `class` = ".is-primary"),
      Buttons("Close", Images.cancel, Snabbdom.event { _ =>
        update(state.copy(edit = None))
      })
    )
  }

  def renderProgramTile(fractal: FractalEntityWithId)
                       (implicit state: LibraryState, update: NutriaState => Unit): VNode =
    h("article.fractal-tile",
      attrs = Seq("title" -> fractal.entity.description),
      events = Seq("click" -> Snabbdom.event(_ => update(state.copy(edit = Some(fractal)))))
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
