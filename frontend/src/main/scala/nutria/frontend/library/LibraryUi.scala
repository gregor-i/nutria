package nutria.frontend.library

import nutria.core._
import nutria.core.viewport.Dimensions
import nutria.frontend.common.{Buttons, Images}
import nutria.frontend.util.LenseUtils
import nutria.frontend.{ExplorerState, LibraryState, NutriaService, NutriaState, common}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object LibraryUi {
  def render(implicit state: LibraryState, update: NutriaState => Unit): VNode = {
    h(tag = "body",
      key = "library")(
      common.Header("Nutria Fractal Library")(state, update),
      h("div.lobby-tile-list")(
        (state.fractals.map(renderProgramTile) ++ Seq.fill(5)(dummyTile) ++ renderPopup().toSeq): _*
      )
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
          footer = Buttons.group(
            Buttons("Apply Changes", Images.upload, Snabbdom.event { _ =>
              org.scalajs.dom.window.alert("currently disabled")
            }, `class` = ".is-primary", disabled = true),

            Buttons("Delete", Images.delete, Snabbdom.event { _ =>
              org.scalajs.dom.window.alert("currently disabled")
            }, `class` = ".is-danger", disabled = true),
            Buttons("Close", Images.cancel, Snabbdom.event { _ =>
              update(state.copy(edit = None))
            })
          )
        )
      )
    }

  def renderProgramTile(fractal: FractalEntityWithId)
                       (implicit state: LibraryState, update: NutriaState => Unit): VNode =
    h("article.fractal-tile",
      attrs = Seq("title" -> fractal.entity.description),
      events = Seq("click" -> Snabbdom.event(_ => update(state.copy(edit = Some(fractal)))))
    )(
      h("img",
        attrs = Seq(
          "width" -> Dimensions.thumbnailDimensions.width.toString,
          "height" -> Dimensions.thumbnailDimensions.height.toString,
          "src" -> s"/api/fractals/${fractal.id}/image"
        )
      )()
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
