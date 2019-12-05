package nutria.frontend.ui

import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.refineV
import nutria.frontend._
import nutria.frontend.ui.common.{Button, CanvasHooks, Icons}
import snabbdom.Snabbdom.h
import snabbdom.{Node, Snabbdom, VNode}

import scala.concurrent.ExecutionContext.Implicits.global

object ExplorerUi {
  def render(implicit state: ExplorerState, update: NutriaState => Unit): VNode =
    h("body",
      key = "explorer")(
      common.Header(state, update),
      renderActionBar(),
      renderCanvas,
    )

  // Actions to implement:
  //  With Fractal Id
  //    Fractal is owned by me
  //      Add Snapshot to fractal     (/)
  //    else
  //      fork and take snapshot      (/)
  //    back to fractal details
  //  return to start position
  //  render high res image and save

  def renderActionBar()
                     (implicit state: ExplorerState, update: NutriaState => Unit): VNode =
    Node("div.buttons")
      .classes("action-bar")
      .childOptional(
        state.fractalId match {
          case Some(fractalId) if state.owned => Some(buttonAddViewport(fractalId))
          case Some(fractalId) => Some(buttonForkAndAddViewport(fractalId))
          case None => None
        }
      )
      .childOptional(
        state.fractalId match {
          case Some(fractalId) => Some(buttonBackToDetails(fractalId))
          case None => None
        }
      )
      .toVNode


  def buttonBackToDetails(fractalId: String)
                         (implicit state: ExplorerState, update: NutriaState => Unit) =
    Button("Edit Parameters", Icons.snapshot, Snabbdom.event { _ =>
      update(LoadingState(NutriaState.detailsState(fractalId)))
    })

  def buttonAddViewport(fractalId: String)
                       (implicit state: ExplorerState, update: NutriaState => Unit) =

    Button("Save this image", Icons.snapshot, Snabbdom.event { _ =>
      for {
        remoteFractal <- NutriaService.loadFractal(fractalId)
        updated = remoteFractal.entity.copy(views = refineV[NonEmpty](remoteFractal.entity.views.value :+ state.fractalImage.view).toOption.get)
        _ <- NutriaService.updateUserFractal(remoteFractal.copy(entity = updated))
      } yield ()
    })
      .classes("is-primary")

  def buttonForkAndAddViewport(fractalId: String)
                              (implicit state: ExplorerState, update: NutriaState => Unit) =

    Button("Fork and Save this image", Icons.copy, Snabbdom.event { _ =>
      for {
        remoteFractal <- NutriaService.loadFractal(fractalId)
        updated = remoteFractal.entity.copy(views = refineV[NonEmpty](remoteFractal.entity.views.value :+ state.fractalImage.view).toOption.get)
        forkedFractal <- NutriaService.save(updated)
      } yield {
        update(ExplorerState(state.user, fractalId = Some(forkedFractal.id), owned = true, fractalImage = state.fractalImage))
      }
    })
      .classes("is-primary")


  def renderCanvas(implicit state: ExplorerState, update: ExplorerState => Unit): VNode =
    h("div.full-size",
      events = ExplorerEvents.canvasMouseEvents ++ ExplorerEvents.canvasWheelEvent ++ ExplorerEvents.canvasTouchEvents
    )(
      h("canvas",
        hooks = CanvasHooks(state.fractalImage, resize = true)
      )()
    )
}
