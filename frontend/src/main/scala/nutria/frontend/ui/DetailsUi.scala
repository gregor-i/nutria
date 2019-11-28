package nutria.frontend.ui

import monocle.{Iso, Lens}
import nutria.core._
import nutria.frontend._
import nutria.frontend.ui.common.{Buttons, Form, FractalImage, Icons}
import nutria.frontend.util.LenseUtils
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
      general(state.fractal, DetailsState.fractalEntity),
      h("h2.title")("Template Settings:"),
      template(state.fractal, DetailsState.fractalEntity),
      h("h2.title")("Parameter Settings:"),
      parameter(state.fractal, DetailsState.fractalEntity),
      h("h2.title")("Snapshots:"),
      snapshots(state.fractal, DetailsState.fractalEntity),
      actions()
    )

  def general(fractal: FractalEntity, lens: Lens[DetailsState, FractalEntity])
             (implicit state: DetailsState, update: NutriaState => Unit) =
    Seq(
      Form.stringInput("Title", lens composeLens FractalEntity.title),
      Form.stringInput("Description", lens composeLens FractalEntity.description),
      Form.stringInput("References", lens composeLens FractalEntity.reference composeIso Iso[List[String], String](_.mkString(" "))(_.split("\\s").filter(_.nonEmpty).toList)),
      Form.intInput("Anti Aliasing", lens composeLens FractalEntity.antiAliase),
    )

  def template(fractal: FractalEntity, lens: Lens[DetailsState, FractalEntity])
              (implicit state: DetailsState, update: NutriaState => Unit) =
    List(
      Form.selectInput(
        label = "Type",
        options = Vector(
          "NewtonIteration",
          "DivergingSeries",
          "DerivedDivergingSeries",
          "FreestyleProgram"
        ),
        value = fractal.program.getClass.getSimpleName,
        onChange = {
          case "NewtonIteration" => update((lens composeLens FractalEntity.program).set(NewtonIteration.default)(state))
          case "DivergingSeries" => update((lens composeLens FractalEntity.program).set(DivergingSeries.default)(state))
          case "DerivedDivergingSeries" => update((lens composeLens FractalEntity.program).set(DerivedDivergingSeries.default)(state))
          case "FreestyleProgram" => update((lens composeLens FractalEntity.program).set(FreestyleProgram.default)(state))
        }
      )
    ) ++ (fractal.program match {
      case f: FreestyleProgram =>
        val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.freestyleProgram.asSetter)
        Seq(
          Form.mulitlineStringInput("template", lensFractal composeLens FreestyleProgram.code),
        )
      case _ => Seq.empty
    })

  def parameter(fractal: FractalEntity, lens: Lens[DetailsState, FractalEntity])
               (implicit state: DetailsState, update: NutriaState => Unit) =
    fractal.program match {
      case f: NewtonIteration =>
        val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.newtonIteration.asSetter)
        Seq(
          Form.stringFunctionInput("function", lensFractal composeLens NewtonIteration.function),
          Form.stringFunctionInput("initial", lensFractal composeLens NewtonIteration.initial),
          Form.intInput("max iterations", lensFractal composeLens NewtonIteration.maxIterations),
          Form.doubleInput("threshold", lensFractal composeLens NewtonIteration.threshold),
          Form.doubleInput("brightness factor", lensFractal composeLens NewtonIteration.brightnessFactor),
          Form.tupleDoubleInput("center", lensFractal composeLens NewtonIteration.center),
          Form.doubleInput("overshoot", lensFractal composeLens NewtonIteration.overshoot),
        )
      case f: DerivedDivergingSeries =>
        val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.derivedDivergingSeries.asSetter)
        Seq(
          Form.intInput("max iterations", lensFractal composeLens DerivedDivergingSeries.maxIterations),
          Form.doubleInput("escape radius", lensFractal composeLens DerivedDivergingSeries.escapeRadius),
          Form.stringFunctionInput("initial Z", lensFractal composeLens DerivedDivergingSeries.initialZ),
          Form.stringFunctionInput("initial Z'", lensFractal composeLens DerivedDivergingSeries.initialZDer),
          Form.stringFunctionInput("iteration Z", lensFractal composeLens DerivedDivergingSeries.iterationZ),
          Form.stringFunctionInput("iteration Z'", lensFractal composeLens DerivedDivergingSeries.iterationZDer),
          Form.doubleInput("h2", lensFractal composeLens DerivedDivergingSeries.h2),
          Form.doubleInput("angle [0, 2pi]", lensFractal composeLens DerivedDivergingSeries.angle),
          Form.colorInput("color inside", lensFractal composeLens DerivedDivergingSeries.colorInside),
          Form.colorInput("color light", lensFractal composeLens DerivedDivergingSeries.colorLight),
          Form.colorInput("color shadow", lensFractal composeLens DerivedDivergingSeries.colorShadow),
        )
      case f: DivergingSeries =>
        val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.divergingSeries.asSetter)
        Seq(
          Form.stringFunctionInput("initial", lensFractal composeLens DivergingSeries.initial),
          Form.stringFunctionInput("iteration", lensFractal composeLens DivergingSeries.iteration),
          Form.intInput("max iterations", lensFractal composeLens DivergingSeries.maxIterations),
          Form.doubleInput("escape radius", lensFractal composeLens DivergingSeries.escapeRadius),
          Form.colorInput("color inside", lensFractal composeLens DivergingSeries.colorInside),
          Form.colorInput("color outside", lensFractal composeLens DivergingSeries.colorOutside),
        )
      case f: FreestyleProgram =>
        val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.freestyleProgram.asSetter)
        f.parameters.indices.map {
          i =>
            Form.stringInput(f.parameters(i).name, lensFractal composeLens FreestyleProgram.parameters composeLens LenseUtils.seqAt[Parameter](i) composeLens Parameter.literal)
        }
    }

  def snapshots(fractal: FractalEntity, lens: Lens[DetailsState, FractalEntity])
               (implicit state: DetailsState, update: NutriaState => Unit) = {
    val viewports = fractal.view :: fractal.alternativeViewports

    val tiles = viewports.map { viewport =>
      h("article.fractal-tile",
        events = Seq("click" -> Snabbdom.event { _ =>
          update(ExplorerState(state.user, None, fractal))
        }),
      )(FractalImage(fractal.copy(view = viewport), Dimensions.thumbnailDimensions))
    }

    h("div.fractal-tile-list")(
      tiles ++ LibraryUi.dummyTiles
    )
  }

  private def actions()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val fractal = state.fractal

    state.user match {
      case Some(user) if user.id == state.remoteFractal.owner =>
        Buttons.group(
          Buttons("Save Changes as new Fractal", Icons.save, Snabbdom.event { _ =>
            val updatedFractal = state.remoteFractal.copy(entity = fractal)
            (for {
              fractalWithId <- NutriaService.save(updatedFractal.entity)
            } yield DetailsState(state.user, fractalWithId, fractalWithId.entity))
              .foreach(update)
          }, `class` = ".is-primary"),
          Buttons("Apply Changes", Icons.save, Snabbdom.event { _ =>
            val updatedFractal = state.remoteFractal.copy(entity = fractal)
            (for {
              _ <- NutriaService.updateUserFractal(updatedFractal)
            } yield state.copy(remoteFractal = updatedFractal))
              .foreach(update)
          }, `class` = ".is-warning"),
          Buttons("Delete", Icons.delete, Snabbdom.event { _ =>
            (for {
              _ <- NutriaService.deleteUserFractal(state.user.get.id, state.remoteFractal.id)
              publicFractals <- NutriaService.loadPublicFractals()
            } yield LibraryState(user = state.user, publicFractals = publicFractals))
              .foreach(update)
          }, `class` = ".is-danger")
        )

      case Some(_) =>
        Buttons.group(
          Buttons("Fork", Icons.copy, Snabbdom.event { _ =>
            (for {
              fractalWithId <- NutriaService.save(fractal)
            } yield DetailsState(state.user, fractalWithId, fractalWithId.entity))
              .foreach(update)
          },
            `class` = ".is-primary")
        )

      case None =>
        Buttons("Login to fork this fractal", "sign-in", Snabbdom.event(_ => ()), `class` = ".is-primary")
    }
  }
}
