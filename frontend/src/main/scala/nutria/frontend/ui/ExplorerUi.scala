package nutria.frontend.ui

import nutria.frontend._
import nutria.frontend.ui.common.CanvasHooks
import snabbdom.Snabbdom.h
import snabbdom.VNode

object ExplorerUi {
  def render(implicit state: ExplorerState, update: NutriaState => Unit): VNode =
    h("body",
      key = "explorer")(
      common.Header(state, update),
      //      renderActionBar(),
      renderCanvas,
    )

  // Actions to implement:
  //  With Fractal Id
  //    Fractal is owned by me
  //      Add Snapshot to fractal
  //    else
  //      fork and take snapshot
  //    back to fractal details
  //  return to start position
  //  render high res image and save

  //  def renderActionBar()
  //                     (implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
  //    h("div.action-bar")(
  //      Buttons("Edit", Images.edit, Snabbdom.event { _ =>
  //        update(state.copy(edit = Some(state.fractalEntity)))
  //      }, `class` = ".is-primary"),
  //      Buttons("Save", Images.upload, Snabbdom.event { _ =>
  //        NutriaService.save(state.fractalEntity)
  //      }),
  //      Buttons("Log Source", Images.info, Snabbdom.event { _ =>
  //        dom.console.log(FractalRenderer.fragmentShaderSource(state.fractalEntity.program, state.fractalEntity.antiAliase))
  //      })
  //    )

  def renderCanvas(implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
    h("div.full-size",
      events = ExplorerEvents.canvasMouseEvents ++ ExplorerEvents.canvasWheelEvent ++ ExplorerEvents.canvasTouchEvents
    )(
      h("canvas",
        hooks = CanvasHooks(state.fractalImage, resize = true)
      )()
    )
}
