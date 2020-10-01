package nutria.frontend

import nutria.api._
import nutria.core.{Dimensions, FractalImage}
import nutria.frontend.pages._
import nutria.frontend.pages.common.FractalTile
import nutria.frontend.service.{FractalService, TemplateService, UserService}
import nutria.frontend.toasts.{ToastType, Toasts}
import nutria.frontend.util.{Untyped, Updatable}
import org.scalajs.dom
import org.scalajs.dom.html.Anchor
import snabbdom.Snabbdom.event
import snabbdom.SnabbdomFacade.Eventlistener

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps
import scala.util.{Failure, Success, Try}
import nutria.frontend.toasts.Syntax._

object Actions {
  private def asyncUpdate(fut: Future[PageState])(implicit updatable: Updatable[_, PageState]): Unit =
    fut.onComplete {
      case Success(value)     => updatable.update(value)
      case Failure(exception) => dom.console.error(s"Unexpected Failure. See: ${exception}")
    }

  private def onlyLoggedIn[A](op: => A)(implicit globalState: GlobalState) =
    globalState.user match {
      case None    => Toasts.dangerToast("Log in first")
      case Some(_) => op
    }

  def exploreFractal()(implicit globalState: GlobalState, updatable: Updatable[GreetingState, PageState]): Eventlistener =
    event { _ =>
      updatable.update(
        ExplorerState(
          remoteFractal = None,
          fractalImage = Entity(value = state.randomFractal)
        )
      )
    }

  def editFractal(
      fractal: WithId[FractalImageEntity]
  )(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      updatable.update(
        DetailsState(remoteFractal = fractal, fractalToEdit = fractal)
      )
    }

  def togglePublishedImage(
      fractal: WithId[FractalImageEntity]
  )(implicit globalState: GlobalState, updatable: Updatable[UserGalleryState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          val published = fractal.entity.published
          (for {
            _ <- FractalService
              .put(
                WithId
                  .entity[FractalImageEntity]
                  .composeLens(Entity.published)
                  .set(!published)
                  .apply(fractal)
              )
            reloaded <- FractalService.loadUserFractals(state.aboutUser)
          } yield state.copy(userFractals = reloaded))
            .pipe { fut =>
              if (published)
                fut.withWarningToast("Unpublishing Fractal", "Fractal unpublished. The fractal will no longer be listed in the public gallery.")
              else
                fut.withSuccessToast("Publishing Fractal", "Fractal published. The fractal will be listed in the public gallery.")
            }
        }
      }
    }

  def deleteFractalFromUserGallery(
      fractalId: String
  )(implicit globalState: GlobalState, updatable: Updatable[UserGalleryState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withWarningToast("Deleting Fractal", "Fractal deleted") {
            for {
              remoteFractal <- FractalService.get(fractalId)
              _             <- FractalService.delete(fractalId)
              reloaded      <- FractalService.loadUserFractals(remoteFractal.owner)
            } yield UserGalleryState(
              userFractals = reloaded,
              aboutUser = remoteFractal.owner,
              page = state.page
            )
          }
        }
      }
    }

  def deleteFractalFromDetails(
      fractalId: String
  )(implicit globalState: GlobalState, updatable: Updatable[DetailsState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withWarningToast("Deleting Fractal", "Fractal deleted") {
            for {
              remoteFractal <- FractalService.get(fractalId)
              _             <- FractalService.delete(fractalId)
              reloaded      <- FractalService.loadUserFractals(remoteFractal.owner)
            } yield UserGalleryState(
              userFractals = reloaded,
              aboutUser = remoteFractal.owner,
              page = 1
            )
          }
        }
      }
    }

  def updateFractal(
      fractalWithId: WithId[FractalImageEntity]
  )(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Updating Fractal", "Fractal updated") {
            for {
              _ <- FractalService.put(fractalWithId)
            } yield DetailsState(
              remoteFractal = fractalWithId,
              fractalToEdit = fractalWithId
            )
          }
        }
      }
    }

  def saveAsNewFractal(
      fractalEntity: FractalImageEntity
  )(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Saving Fractal", "Fractal saved") {
            for {
              fractalWithId <- FractalService.post(fractalEntity.copy(published = false))
            } yield DetailsState(
              remoteFractal = fractalWithId,
              fractalToEdit = fractalWithId
            )
          }
        }
      }
    }

  def saveSnapshot(
      fractalEntity: FractalImageEntity
  )(implicit globalState: GlobalState, updatable: Updatable[ExplorerState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Saving Fractal", "Fractal saved") {
            for {
              savedImage <- FractalService.post(fractalEntity.copy(published = false))
            } yield state.copy(remoteFractal = Some(savedImage))
          }
        }
      }
    }

  def saveTemplate(
      templateEntity: FractalTemplateEntity
  )(implicit globalState: GlobalState, updatable: Updatable[TemplateEditorState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Saving Template", "Template saved") {
            for {
              savedTemplate <- TemplateService.post(templateEntity)
            } yield state.copy(remoteTemplate = Some(savedTemplate))
          }
        }
      }
    }

  def updateTemplate(
      template: FractalTemplateEntityWithId
  )(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Updating Template", "Template updated") {
            for {
              _ <- TemplateService.put(template)
            } yield TemplateEditorState.byTemplate(template)
          }
        }
      }
    }

  def deleteTemplate(templateId: String)(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withWarningToast("Deleting Template", "Template deleted") {
            for {
              _         <- TemplateService.delete(templateId)
              templates <- TemplateService.listUser(globalState.user.get.id)
            } yield TemplateGalleryState(templates = templates)
          }
        }
      }
    }

  def togglePublishedTemplate(
      template: FractalTemplateEntityWithId
  )(implicit globalState: GlobalState, updatable: Updatable[TemplateGalleryState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          val published = template.entity.published
          (for {
            _ <- TemplateService
              .put(
                WithId
                  .entity[FractalTemplateEntity]
                  .composeLens(Entity.published)
                  .set(!published)
                  .apply(template)
              )
            templates <- TemplateService.listUser(globalState.user.get.id)
          } yield state.copy(templates = templates))
            .pipe { fut =>
              if (published)
                fut.withWarningToast("Unpublishing Template", "Template unpublished. The template will no longer be listed in the public gallery.")
              else
                fut.withSuccessToast("Publishing Template", "Template published. The template will be listed in the public gallery.")
            }
        }
      }
    }

  def deleteUser(
      userId: String
  )(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Deleting account", "Good Bye!") {
            for {
              _     <- UserService.delete(userId)
              state <- Links.greetingState()
            } yield state
          }
        }
      }
    }

  def openSaveToDiskModal(implicit globalState: GlobalState, updatable: Updatable[ExplorerState, PageState]): Eventlistener =
    event { _ =>
      updatable.update {
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

  def closeSaveToDiskModal(implicit globalState: GlobalState, updatable: Updatable[ExplorerState, PageState]): Eventlistener =
    event { _ =>
      updatable.update {
        state.copy(saveModal = None)
      }
    }

  def saveToDisk(
      fractalImage: FractalImage,
      dimensions: Dimensions
  )(implicit globalState: GlobalState, updatable: Updatable[PageState, PageState]): Eventlistener =
    event { _ =>
      val dataUrl = FractalTile.dataUrl(fractalImage, dimensions)

      val link = dom.document.createElement("a").asInstanceOf[Anchor]
      Untyped(link).download = "fractal-image.png"
      link.href = dataUrl
      dom.document.body.appendChild(link)
      link.click()
      dom.document.body.removeChild(link)
    }
}
