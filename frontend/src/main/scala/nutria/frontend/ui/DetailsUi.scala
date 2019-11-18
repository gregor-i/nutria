package nutria.frontend.ui

import nutria.frontend.ui.common.{Buttons, Images, RenderEditFractalEntity}
import nutria.frontend.{DetailsState, LibraryState, NutriaService, NutriaState}
import snabbdom.Snabbdom
import snabbdom.Snabbdom.h

import scala.concurrent.ExecutionContext.Implicits.global

object DetailsUi {
  def render(implicit state: DetailsState, update: NutriaState => Unit) =
    h("body",
      key = "explorer")(
      common.Header("Nutria Fractal Explorer")(state, update),
      body,
      common.Footer()
    )

  def body(implicit state: DetailsState, update: NutriaState => Unit) =
    h("div", styles = Seq("margin" -> "auto", "max-width" -> "848px"))(
      h("h2.title")("General Settings:"),
      RenderEditFractalEntity.generalBody(state.fractal, DetailsState.fractalEntity),
      h("h2.title")("Template Settings:"),
      RenderEditFractalEntity.templateBody(state.fractal, DetailsState.fractalEntity),
      h("h2.title")("Parameter Settings:"),
      RenderEditFractalEntity.parametersBody(state.fractal, DetailsState.fractalEntity),
      h("h2.title")("Snapshots:"),
      h("div.fractal-tile-list")(
        RenderEditFractalEntity.snapshotsBody(state.fractal, DetailsState.fractalEntity)
      ),
      actions()
    )

  private def actions()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val fractal = state.fractal
    if (state.user.map(_.id).contains(state.remoteFractal.owner)) {
      Buttons.group(
        Buttons("Apply Changes", Images.upload, Snabbdom.event { _ =>
          val updatedFractal = state.remoteFractal.copy(entity = fractal)
          (for {
            _ <- NutriaService.updateUserFractal(updatedFractal)
          } yield state.copy(remoteFractal = updatedFractal))
            .foreach(update)
        }, `class` = ".is-primary"),
        Buttons("Delete", Images.delete, Snabbdom.event { _ =>
          (for {
            _ <- NutriaService.deleteUserFractal(state.user.get.id, state.remoteFractal.id)
            publicFractals <- NutriaService.loadPublicFractals()
          } yield LibraryState(user = state.user, publicFractals = publicFractals))
            .foreach(update)
        }, `class` = ".is-danger")
      )
    } else {
      Buttons.group(
        Buttons("Fork", Images.copy, Snabbdom.event(_ => println("todo: fork!")),
          `class` = ".is-primary")
      )
    }
  }
}
