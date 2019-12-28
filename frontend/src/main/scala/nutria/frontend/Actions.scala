package nutria.frontend

import snabbdom._
import SnabbdomFacade.Eventlistener
import Snabbdom.event
import eu.timepit.refined.collection.NonEmpty
import nutria.core.viewport.Viewport
import nutria.core.{FractalEntity, FractalEntityWithId, FractalImage}
import eu.timepit.refined.refineV

import scala.concurrent.ExecutionContext.Implicits.global

object Actions {
  def loadGallery(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(LoadingState(NutriaState.libraryState()))
    }

  def exploreFractal(
      fractal: FractalEntityWithId
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(
        ExplorerState(
          user = state.user,
          fractalId = Some(fractal.id),
          owned = state.user.exists(_.id == fractal.owner),
          fractalImage = FractalImage.firstImage(fractal.entity)
        )
      )
    }

  def editFractal(
      fractal: FractalEntityWithId
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(
        DetailsState(user = state.user, remoteFractal = fractal, fractalToEdit = fractal)
      )
    }

  def loadAndEditFractal(
      id: String
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(LoadingState(NutriaState.detailsState(id)))
    }

  def addViewport(
      fractalId: String,
      viewport: Viewport
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      for {
        remoteFractal <- NutriaService.loadFractal(fractalId)
        views   = (remoteFractal.entity.views.value :+ viewport).distinct
        updated = remoteFractal.entity.copy(views = refineV[NonEmpty](views).toOption.get)
        _ <- NutriaService.updateFractal(remoteFractal.copy(entity = updated))
      } yield ()
    }

  def forkAndAddViewport(
      fractalId: String,
      viewport: Viewport
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      for {
        remoteFractal <- NutriaService.loadFractal(fractalId)
        updated = remoteFractal.entity.copy(
          views = refineV[NonEmpty](
            remoteFractal.entity.views.value :+ viewport
          ).toOption.get
        )
        forkedFractal <- NutriaService.save(updated)
      } yield {
        update(
          ExplorerState(
            state.user,
            fractalId = Some(forkedFractal.id),
            owned = true,
            fractalImage =
              FractalImage(remoteFractal.entity.program, viewport, remoteFractal.entity.antiAliase)
          )
        )
      }
    }
}
