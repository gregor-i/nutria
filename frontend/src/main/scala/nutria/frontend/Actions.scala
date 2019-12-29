package nutria.frontend

import snabbdom._
import SnabbdomFacade.Eventlistener
import Snabbdom.event
import eu.timepit.refined.collection.NonEmpty
import nutria.core.viewport.Viewport
import nutria.core.{FractalEntity, FractalEntityWithId, FractalImage}
import eu.timepit.refined.refineV
import nutria.frontend.ui.common.Header
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  def exploreFractal(
      fractal: FractalImage
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(
        ExplorerState(
          user = state.user,
          owned = false,
          fractalId = None,
          fractalImage = fractal
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

  def togglePublished(
      fractal: FractalEntityWithId
  )(implicit state: UserLibraryState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      (for {
        _ <- NutriaService.updateFractal(
          FractalEntityWithId.entity
            .composeLens(FractalEntity.published)
            .modify(!_)
            .apply(fractal)
        )
        reloaded <- NutriaService.loadUserFractals(state.aboutUser)
      } yield state.copy(userFractals = reloaded))
        .foreach(update)
    }

  def deleteFractal(
      fractalId: String
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      // todo: add alert or dialog
      (for {
        remoteFractal <- NutriaService.loadFractal(fractalId)
        _             <- NutriaService.deleteFractal(fractalId)
        reloaded      <- NutriaService.loadUserFractals(remoteFractal.owner)
      } yield UserLibraryState(
        user = state.user,
        userFractals = reloaded,
        aboutUser = remoteFractal.owner
      )).foreach(update)
    }

  def moveViewportUp(
      viewport: Viewport
  )(implicit state: DetailsState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      val lensViewports = DetailsState.fractalToEdit.composeLens(FractalEntityWithId.viewports)
      val views         = lensViewports.get(state).value
      val newViewports  = views.filter(_ == viewport) ++ views.filter(_ != viewport)
      refineV[NonEmpty](newViewports) match {
        case Right(newViews) => update(lensViewports.set(newViews)(state))
        case Left(_)         => ???
      }
    }

  def deleteViewport(
      viewport: Viewport
  )(implicit state: DetailsState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      val lensViewports = DetailsState.fractalToEdit.composeLens(FractalEntityWithId.viewports)
      val views         = lensViewports.get(state).value
      val newViewports  = views.filter(_ != viewport)
      refineV[NonEmpty](newViewports) match {
        case Right(newViews) => update(lensViewports.set(newViews)(state))
        case Left(_)         => dom.window.alert("the last snapshot can't be deleted.")
      }
    }

  def updateFractal(
      fractalWithId: FractalEntityWithId
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      (for {
        _ <- NutriaService.updateFractal(fractalWithId)
      } yield DetailsState(
        user = state.user,
        remoteFractal = fractalWithId,
        fractalToEdit = fractalWithId
      )).foreach(update)
    }

  def saveAsNewFractal(
      fractalEntity: FractalEntity
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      (for {
        fractalWithId <- NutriaService.save(fractalEntity)
      } yield DetailsState(
        user = state.user,
        remoteFractal = fractalWithId,
        fractalToEdit = fractalWithId
      )).foreach(update)
    }

  def login(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    Snabbdom.event(_ => dom.window.location.href = Header.loginHref)

}
