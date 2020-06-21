package nutria.frontend

import nutria.api._
import nutria.core.{Dimensions, FractalImage, Viewport}
import nutria.frontend.pages._
import nutria.frontend.pages.common.FractalTile
import nutria.frontend.service.NutriaService
import nutria.frontend.toasts.Toasts
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Anchor
import snabbdom.Snabbdom.event
import snabbdom.SnabbdomFacade.Eventlistener

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Actions {
  private def asyncUpdate(fut: Future[NutriaState])(implicit update: NutriaState => Unit): Unit =
    fut.onComplete {
      case Success(value) => update(value)
      case Failure(exception) =>
        dom.console.error(s"Unexpected Failure. See: ${exception}")
        Toasts.dangerToast("The last action was not successful. Please retry the action or reload the page.")
    }

  private def onlyLoggedIn[A](op: => A)(implicit state: NutriaState) =
    state.user match {
      case None    => Toasts.dangerToast("Log in first")
      case Some(_) => op
    }

  def exploreFractal()(implicit state: GreetingState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(
        ExplorerState(
          user = state.user,
          remoteFractal = None,
          fractalImage = Entity(value = state.randomFractal)
        )
      )
    }

  def editFractal(
      fractal: WithId[FractalImageEntity]
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update(
        DetailsState(user = state.user, remoteFractal = fractal, fractalToEdit = fractal)
      )
    }

  def addViewport(
      fractalId: String,
      viewport: Viewport
  )(implicit state: ExplorerState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            remoteFractal <- NutriaService.loadFractal(fractalId)
            newFractal = Entity.value
              .composeLens(FractalImage.viewport)
              .set(viewport)(remoteFractal.entity)
            savedFractal <- NutriaService.save(newFractal)
            _ = Toasts.successToast("Snapshot saved")
          } yield ExplorerState(
            user = state.user,
            remoteFractal = Some(savedFractal),
            fractalImage = savedFractal.entity
          )
        }
      }
    }

  // todo: rename
  def forkAndAddViewport(
      fractalId: String,
      viewport: Viewport
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            remoteFractal <- NutriaService.loadFractal(fractalId)
            updated = Entity.value
              .composeLens(FractalImage.viewport)
              .set(viewport)(remoteFractal.entity)
            forkedFractal <- NutriaService.save(updated)
            _ = Toasts.successToast("Fractal saved")
          } yield ExplorerState(
            state.user,
            remoteFractal = Some(forkedFractal),
            fractalImage = forkedFractal.entity
          )
        }
      }
    }

  def togglePublished(
      fractal: WithId[FractalImageEntity]
  )(implicit state: UserGalleryState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          val published = fractal.entity.published
          for {
            _ <- NutriaService.updateFractal(
              WithId
                .entity[FractalImageEntity]
                .composeLens(Entity.published)
                .set(!published)
                .apply(fractal)
            )
            reloaded <- NutriaService.loadUserFractals(state.aboutUser)
            _ = if (published)
              Toasts.warningToast(
                "Fractal unpublished. The fractal will no longer be listed in the public gallery."
              )
            else
              Toasts.successToast(
                "Fractal unpublished. The fractal will be listed in the public gallery."
              )
          } yield state.copy(userFractals = reloaded)
        }
      }
    }

  def deleteFractalFromUserGallery(
      fractalId: String
  )(implicit state: UserGalleryState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            remoteFractal <- NutriaService.loadFractal(fractalId)
            _             <- NutriaService.deleteFractal(fractalId)
            _ = Toasts.warningToast("Fractal deleted.")
            reloaded <- NutriaService.loadUserFractals(remoteFractal.owner)
          } yield UserGalleryState(
            user = state.user,
            userFractals = reloaded,
            aboutUser = remoteFractal.owner,
            page = state.page
          )
        }
      }
    }

  def deleteFractalFromDetails(
      fractalId: String
  )(implicit state: DetailsState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            remoteFractal <- NutriaService.loadFractal(fractalId)
            _             <- NutriaService.deleteFractal(fractalId)
            _ = Toasts.warningToast("Fractal deleted.")
            reloaded <- NutriaService.loadUserFractals(remoteFractal.owner)
          } yield UserGalleryState(
            user = state.user,
            userFractals = reloaded,
            aboutUser = remoteFractal.owner,
            page = 1
          )
        }
      }
    }

  def updateFractal(
      fractalWithId: WithId[FractalImageEntity]
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            _ <- NutriaService.updateFractal(fractalWithId)
            _ = Toasts.successToast("Fractal updated.")
          } yield DetailsState(
            user = state.user,
            remoteFractal = fractalWithId,
            fractalToEdit = fractalWithId
          )
        }
      }
    }

  def saveAsNewFractal(
      fractalEntity: FractalImageEntity
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            fractalWithId <- NutriaService.save(fractalEntity)
            _ = Toasts.successToast("Fractal saved.")
          } yield DetailsState(
            user = state.user,
            remoteFractal = fractalWithId,
            fractalToEdit = fractalWithId
          )
        }
      }
    }

  def saveSnapshot(fractalEntity: FractalImageEntity)(implicit state: ExplorerState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            savedImage <- NutriaService.save(fractalEntity.copy(published = false))
            _ = Toasts.successToast("Fractal saved.")
          } yield state.copy(remoteFractal = Some(savedImage))
        }
      }
    }

  def saveTemplate(templateEntity: FractalTemplateEntity)(implicit state: TemplateEditorState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            savedTemplate <- NutriaService.saveTemplate(templateEntity)
            _ = Toasts.successToast("Template saved.")
          } yield state.copy(remoteTemplate = Some(savedTemplate))
        }
      }
    }

  def updateTemplate(
      template: FractalTemplateEntityWithId
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            _ <- NutriaService.updateTemplate(template)
            _ = Toasts.successToast("Template updated.")
          } yield TemplateEditorState.byTemplate(template)
        }
      }
    }

  def deleteTemplate(templateId: String)(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            _         <- NutriaService.deleteTemplate(templateId)
            templates <- NutriaService.loadUserTemplates(state.user.get.id)
          } yield TemplateGalleryState(user = state.user, templates = templates)
        }
      }
    }

  def togglePublished(
      template: FractalTemplateEntityWithId
  )(implicit state: TemplateGalleryState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          val published = template.entity.published
          for {
            _ <- NutriaService.updateTemplate(
              WithId
                .entity[FractalTemplateEntity]
                .composeLens(Entity.published)
                .set(!published)
                .apply(template)
            )
            templates <- NutriaService.loadUserTemplates(state.user.get.id)
            _ = if (published)
              Toasts.warningToast(
                "Template unpublished. The template will no longer be listed in the public gallery."
              )
            else
              Toasts.successToast(
                "Template unpublished. The template will be listed in the public gallery."
              )
          } yield state.copy(templates = templates)
        }
      }
    }

  def deleteUser(
      userId: String
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          for {
            _     <- NutriaService.deleteUser(userId)
            state <- Links.greetingState(None)
            _ = Toasts.successToast("Good Bye")
          } yield state
        }
      }
    }

  def openSaveToDiskModal(implicit state: ExplorerState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update {
        state.copy(
          saveModal = Some(
            SaveFractalDialog(
              dimensions = Dimensions.fullHD,
              antiAliase = state.fractalImage.value.antiAliase
            )
          )
        )
      }
    }

  def closeSaveToDiskModal(implicit state: ExplorerState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      update {
        state.copy(saveModal = None)
      }
    }

  def saveToDisk(
      fractalImage: FractalImage,
      dimensions: Dimensions
  )(implicit state: NutriaState, update: NutriaState => Unit): Eventlistener =
    event { _ =>
      val dataUrl = FractalTile.dataUrl(fractalImage, dimensions)

      val link = dom.document.createElement("a").asInstanceOf[Anchor]
      Untyped(link).download = "fractal-image.png"
      link.href = dataUrl
      dom.document.body.appendChild(link)
      link.click()
      dom.document.body.removeChild(link)
    }

  def gotoFAQ()(implicit state: NutriaState): FAQState =
    FAQState(state.user)
}
