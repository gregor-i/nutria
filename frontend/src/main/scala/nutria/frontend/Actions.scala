package nutria.frontend

import nutria.api._
import nutria.core.{Dimensions, FractalImage}
import nutria.frontend.pages._
import nutria.frontend.pages.common.FractalTile
import nutria.frontend.service.{FractalService, TemplateService, UserService}
import nutria.frontend.toasts.Syntax._
import nutria.frontend.toasts.Toasts
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Anchor
import snabbdom.Snabbdom.event
import snabbdom.SnabbdomFacade.Eventlistener

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps
import scala.util.{Failure, Success}

object Actions extends ExecutionContext {
  private def asyncUpdate(fut: Future[PageState])(implicit context: Context[_]): Unit =
    fut.onComplete {
      case Success(value)     => context.update(value)
      case Failure(exception) => dom.console.error(s"Unexpected Failure. See: ${exception}")
    }

  private def asyncUpdateBoth(fut: Future[(GlobalState, PageState)])(implicit context: Context[_]): Unit =
    fut.onComplete {
      case Success((global, local)) => context.update(global, local)
      case Failure(exception)       => dom.console.error(s"Unexpected Failure. See: ${exception}")
    }

  private def onlyLoggedIn[A](op: => A)(implicit context: Context[_]) =
    context.global.user match {
      case None    => Toasts.dangerToast("Log in first")
      case Some(_) => op
    }

  def togglePublishedImage(fractal: WithId[FractalImageEntity])(implicit context: Context[UserGalleryState]): Eventlistener =
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
            reloaded <- FractalService.loadUserFractals(context.local.aboutUser)
          } yield context.local.copy(userFractals = reloaded))
            .pipe { fut =>
              if (published)
                fut.withWarningToast("Unpublishing Fractal", "Fractal unpublished. The fractal will no longer be listed in the public gallery.")
              else
                fut.withSuccessToast("Publishing Fractal", "Fractal published. The fractal will be listed in the public gallery.")
            }
        }
      }
    }

  def deleteFractalFromUserGallery(fractalId: String)(implicit context: Context[UserGalleryState]): Eventlistener =
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
              page = context.local.page
            )
          }
        }
      }
    }

  def deleteFractal(fractalId: String)(implicit context: Context[_]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withWarningToast("Deleting Fractal", "Fractal deleted") {
            for {
              _        <- FractalService.delete(fractalId)
              newState <- Links.userGalleryState(userId = context.global.user.get.id)
            } yield newState
          }
        }
      }
    }

  def updateFractal(fractalWithId: WithId[FractalImageEntity])(implicit context: Context[_]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Updating Fractal", "Fractal updated") {
            for {
              _ <- FractalService.put(fractalWithId)
            } yield Links.explorerStateWithModal(fractalWithId)
          }
        }
      }
    }

  def saveAsNewFractal(fractalEntity: FractalImageEntity)(implicit context: Context[_]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Saving Fractal", "Fractal saved") {
            for {
              fractalWithId <- FractalService.post(fractalEntity.copy(published = false))
            } yield Links.explorerStateWithModal(fractalWithId)
          }
        }
      }
    }

  def saveSnapshot(fractalEntity: FractalImageEntity)(implicit context: Context[ExplorerState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Saving Fractal", "Fractal saved") {
            for {
              savedImage <- FractalService.post(fractalEntity.copy(published = false))
            } yield context.local.copy(remoteFractal = Some(savedImage))
          }
        }
      }
    }

  def saveTemplate(templateEntity: FractalTemplateEntity)(implicit context: Context[TemplateEditorState]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withSuccessToast("Saving Template", "Template saved") {
            for {
              savedTemplate <- TemplateService.post(templateEntity)
            } yield context.local.copy(remoteTemplate = Some(savedTemplate))
          }
        }
      }
    }

  def updateTemplate(template: FractalTemplateEntityWithId)(implicit context: Context[_]): Eventlistener =
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

  def deleteTemplate(templateId: String)(implicit context: Context[_]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdate {
          withWarningToast("Deleting Template", "Template deleted") {
            for {
              _         <- TemplateService.delete(templateId)
              templates <- TemplateService.listUser(context.global.user.get.id)
            } yield TemplateGalleryState(templates = templates)
          }
        }
      }
    }

  def togglePublishedTemplate(template: FractalTemplateEntityWithId)(implicit context: Context[TemplateGalleryState]): Eventlistener =
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
            templates <- TemplateService.listUser(context.global.user.get.id)
          } yield context.local.copy(templates = templates))
            .pipe { fut =>
              if (published)
                fut.withWarningToast("Unpublishing Template", "Template unpublished. The template will no longer be listed in the public gallery.")
              else
                fut.withSuccessToast("Publishing Template", "Template published. The template will be listed in the public gallery.")
            }
        }
      }
    }

  def deleteUser(userId: String)(implicit context: Context[_]): Eventlistener =
    event { _ =>
      onlyLoggedIn {
        asyncUpdateBoth {
          withSuccessToast("Deleting account", "Good Bye!") {
            for {
              _     <- UserService.delete(userId)
              state <- Links.greetingState()
            } yield (GlobalState.initial, state)
          }
        }
      }
    }

  def saveToDisk(fractalImage: FractalImage, dimensions: Dimensions)(implicit context: Context[_]): Eventlistener =
    event { _ =>
      val dataUrl = FractalTile.dataUrl(fractalImage, dimensions)
      val link    = dom.document.createElement("a").asInstanceOf[Anchor]
      Untyped(link).download = "fractal-image.png"
      link.href = dataUrl
      dom.document.body.appendChild(link)
      link.click()
      dom.document.body.removeChild(link)
    }
}
