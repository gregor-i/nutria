package nutria.frontend.explorer

import nutria.core.User
import nutria.frontend.common.{Buttons, CanvasHooks, Images}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.LenseUtils
import nutria.frontend._
import org.scalajs.dom
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object ExplorerUi {
  def render(implicit state: ExplorerState, user: Option[User], update: NutriaState => Unit): VNode =
    h("body",
      key = "explorer")(
      common.Header("Nutria Fractal Explorer", user)(state, update),
      renderActionBar(),
      renderCanvas,
      renderPopup().getOrElse[VNode](h("span", styles = Seq("display" -> "none"))())
    )

  def renderActionBar()
                     (implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
    h("div.action-bar")(
      Buttons("Edit", Images.edit, Snabbdom.event { _ =>
        update(state.copy(edit = Some(state.fractalEntity)))
      }, `class` = ".is-primary"),
      Buttons("Save", Images.upload, Snabbdom.event { _ =>
        NutriaService.save(state.fractalEntity)
      }),
      Buttons("Log Source", Images.info, Snabbdom.event { _ =>
        dom.console.log(FractalRenderer.fragmentShaderSource(state.fractalEntity.program, state.fractalEntity.antiAliase))
      })
    )

  def renderPopup()
                 (implicit state: ExplorerState, update: ExplorerState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      h("div.modal.is-active")(
        h("div.modal-background",
          events = Seq("click" ->
            Snabbdom.event(_ => update(state.copy(edit = None)))
          )
        )(),
        common.RenderEditFractalEntity(
          fractal = fractal,
          currentTab = state.tab,
          lens = LenseUtils.lookedUp(fractal, ExplorerState.editOptional.asSetter),
          lensTab = ExplorerState.tab,
          footer = Buttons.group(
            Buttons("Accept", Images.check, Snabbdom.event { _ =>
              update(state.copy(fractalEntity = fractal, edit = None))
            }, `class` = ".is-primary"),
            Buttons("Cancel", Images.cancel, Snabbdom.event { _ =>
              update(state.copy(edit = None))
            })
          )
        )
      )
    }

  def renderCanvas(implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
    h("div.full-size",
      events = ExplorerEvents.canvasMouseEvents ++ ExplorerEvents.canvasWheelEvent ++ ExplorerEvents.canvasTouchEvents
    )(
      h("canvas",
        hooks = CanvasHooks(state.fractalEntity, resize = true)
      )()
    )
}
