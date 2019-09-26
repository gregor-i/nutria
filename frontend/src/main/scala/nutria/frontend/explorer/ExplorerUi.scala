package nutria.frontend.explorer

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.frontend.common.{Buttons, CanvasHooks}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{LenseUtils, SnabbdomHelper}
import nutria.frontend.{ExplorerState, NutriaService, NutriaState, common}
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object ExplorerUi {
  def render(implicit state: ExplorerState, update: NutriaState => Unit): VNode =
    tags.div(
      key := "explorer",
      common.Header("Nutria Fractal Explorer")(state, update),
      renderActionBar(),
      renderCanvas,
      renderPopup()
    )

  def renderActionBar()
                     (implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
    tags.div(
      attrs.className := "action-bar",
      Buttons.edit(
        attrs.className := "button is-primary",
        events.onClick := (() => update(state.copy(edit = Some(state.fractalEntity))))
      ),
      Buttons.save(
        attrs.className := (state.saveProcess.map(_.value) match {
          case Some(None) => "button is-loading"
          case Some(Some(Success(_))) => "button is-success"
          case Some(Some(Failure(_))) => "button is-danger"
          case None => "button"
        }),
        attrs.disabled := state.saveProcess.exists(!_.isCompleted),
        events.onClick := (() =>
          update(state.copy(saveProcess = Some(NutriaService.save(state.fractalEntity).map(_ => state.fractalEntity))))
          ),
      ),
      Buttons.share(),
      Buttons.logSource(
        events.onClick := (() => dom.console.log(FractalRenderer.fragmentShaderSource(state.fractalEntity.program, state.fractalEntity.antiAliase)))
      )
    )

  def renderPopup()
                 (implicit state: ExplorerState, update: ExplorerState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      tags.div(
        attrs.className := "modal is-active",
        tags.div(
          attrs.className := "modal-background",
          events.onClick := (() => update(state.copy(edit = None))),
        ),
        common.RenderEditFractalEntity(
          fractal = fractal,
          lens = LenseUtils.lookedUp(fractal, ExplorerState.editOptional.asSetter),
          footer = Buttons.group(
            Buttons.accept(
              attrs.className := "button is-primary",
              events.onClick := (() => update(state.copy(fractalEntity = fractal, edit = None)))
            ),
            Buttons.cancel(
              events.onClick := (() => update(state.copy(edit = None)))
            )
          )
        )
      )
    }

  def renderCanvas(implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
    tags.div(
      attrs.className := "full-size",
      tags.canvas(
        CanvasHooks(state.fractalEntity, resize = true)
      ),
      SnabbdomHelper.seq(ExplorerEvents.canvasMouseEvents),
      SnabbdomHelper.seq(ExplorerEvents.canvasTouchEvents),
      ExplorerEvents.canvasWheelEvent
    )
}
