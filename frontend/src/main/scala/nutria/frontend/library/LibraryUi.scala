package nutria.frontend.library

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.core._
import nutria.frontend.common.Buttons
import nutria.frontend.util.{LenseUtils, SnabbdomHelper}
import nutria.frontend.{ExplorerState, LibraryState, NutriaState, common}

object LibraryUi extends SnabbdomHelper {
  def render(implicit state: LibraryState, update: NutriaState => Unit): VNode = {
    tags.div(
      key := "library",
      common.Header("Nutria Fractal Library")(state, update),
      tags.div(
        attrs.className := "lobby-tile-list",
        seqNode(state.fractals.map(renderProgramTile)),
        seqNode(Seq.fill(5)(dummyTile))
      ),
      renderPopup(),
    )
  }

  def renderPopup()
                 (implicit state: LibraryState, update: NutriaState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      tags.div(
        attrs.className := "modal is-active",
        tags.div(
          attrs.className := "modal-background",
          events.onClick := (() => update(state.copy(edit = None))),
        ),
        common.RenderEditFractalEntity(
          fractal = fractal.entity,
          lens = LenseUtils.lookedUp(fractal.entity, LibraryState.editOptional.composeLens(FractalEntityWithId.entity).asSetter),
          footer = Buttons.group(
            Buttons.explore(
              attrs.className := "button is-primary",
              events.onClick := (() => update(ExplorerState(fractals = state.fractals, fractalEntity = fractal.entity, initialEntity = fractal)))
            ),
            Buttons.cancel(
              attrs.className := "button",
              events.onClick := (() => update(state.copy(edit = None)))
            ),
            /*Buttons.delete(
              attrs.className := "button is-danger",
              events.onClick := (() => {
                NutriaService.delete(fractal.id)
                  .foreach(newFractals => update(state.copy(programs = newFractals, edit = None)))
              })
            )*/
          )
        )
      )
    }

  def renderProgramTile(fractal: FractalEntityWithId)
                       (implicit state: LibraryState, update: NutriaState => Unit): VNode =
    tags.build("article")(
      events.onClick := (() => update(state.copy(edit = Some(fractal)))),
      tags.img(
        attrs.widthAttr := 400,
        attrs.heightAttr := 225,
        attrs.src := s"/api/fractals/${fractal.id}/image"
      ),
      attrs.title := fractal.entity.description
    )


  val dummyTile =
    tags.build("article")(
      attrs.className := "dummy-tile",
      tags.div(
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 0,
          styles.display := "block",
        )
      )
    )
}
