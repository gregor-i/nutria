package nutria.frontend.ui

import monocle.Lens
import nutria.core.viewport.{Dimensions, Viewport, ViewportList}
import nutria.core.{DivergingSeries, FractalEntity, FractalImage, FractalProgram, NewtonIteration}
import nutria.frontend.CreateNewFractalState.FormulaStep
import nutria.frontend.ui.common._
import nutria.frontend.util.LenseUtils
import nutria.frontend.{Actions, CreateNewFractalState, NutriaState}
import nutria.macros.StaticContent
import snabbdom.Node

import scala.util.chaining._

object CreateNewFractalUI extends Page[CreateNewFractalState] {
  override def render(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Body()
      .child(Header())
      .child(
        Node("div.container")
          .child(Node("section.section").child(Node("h1.title.is-1").text("Create new Fractal")))
          .child(selectTyp())
          .childOptional(
            state.step match {
              case FormulaStep(series: DivergingSeries) => Some(selectFormulaDivergingSeries(series))
              case FormulaStep(series: NewtonIteration) => Some(selectFormulaNewtonIteration(series))
              case _                                    => None
            }
          )
      )
      .child(Footer())

  // step 1
  private def selectTyp()(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 1: Select the fractal type"))
      .child(
        Node("div.fractal-tile-list")
          .child(
            Link(CreateNewFractalState(state.user, FormulaStep(DivergingSeries.default)))
              .classes("fractal-tile")
              .child(Images(Images.exampleDivergingSeries))
          )
          .child(
            Link(CreateNewFractalState(state.user, FormulaStep(NewtonIteration.default)))
              .classes("fractal-tile")
              .child(Images(Images.exampleNewtonIteration))
          )
      )

  // step 2
  private def selectFormulaDivergingSeries(
      series: DivergingSeries
  )(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node = {
    val lens: Lens[CreateNewFractalState, DivergingSeries] = CreateNewFractalState.step
      .composePrism(CreateNewFractalState.formulaStep)
      .composeLens(CreateNewFractalState.FormulaStep.program)
      .pipe(LenseUtils.subclass(_, FractalProgram.divergingSeries, series))

    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 2: Select the formula"))
      .child(
        Node("div.content")
          .child(
            Node("p")
              .prop("innerHTML", StaticContent("frontend/src/main/html/diverging_series_fractal_introduction.html"))
          )
          .child(languageInformation)
      )
      .child(
        Form.stringFunctionInput("initial(lambda)", lens.composeLens(DivergingSeries.initial))
      )
      .child(
        Form.stringFunctionInput("iteration(z, lambda)", lens.composeLens(DivergingSeries.iteration))
      )
      .child(
        FractalTile(
          FractalImage(program = series, view = Viewport.mandelbrot),
          dimensions = Dimensions.thumbnail.scale(1.5)
        ).classes("fractal-tile")
          .style("display", "block")
          .style("margin", "8px auto")
      )
      .child(
        finishButton(
          FractalEntity(
            program = series,
            views = ViewportList.refineUnsafe(List(Viewport.mandelbrot))
          )
        )
      )
  }

  private def selectFormulaNewtonIteration(
      series: NewtonIteration
  )(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node = {
    val lens: Lens[CreateNewFractalState, NewtonIteration] = CreateNewFractalState.step
      .composePrism(CreateNewFractalState.formulaStep)
      .composeLens(CreateNewFractalState.FormulaStep.program)
      .pipe(LenseUtils.subclass(_, FractalProgram.newtonIteration, series))

    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 2: Select the formula"))
      .child(
        Node("div.content")
          .child(Node("p").prop("innerHTML", StaticContent("frontend/src/main/html/newton_fractal_introduction.html")))
          .child(languageInformation)
      )
      .child(
        Form.stringFunctionInput("initial(lambda)", lens.composeLens(NewtonIteration.initial))
      )
      .child(
        Form.stringFunctionInput("iteration(z, lambda)", lens.composeLens(NewtonIteration.function))
      )
      .child(
        FractalTile(
          FractalImage(program = series, view = Viewport.aroundZero),
          dimensions = Dimensions.thumbnail.scale(1.5)
        ).classes("fractal-tile")
          .style("display", "block")
          .style("margin", "8px auto")
      )
      .child(
        finishButton(
          FractalEntity(
            program = series,
            views = ViewportList.refineUnsafe(List(Viewport.aroundZero))
          )
        )
      )
  }

  private def languageInformation(implicit nutriaState: NutriaState, update: NutriaState => Unit) =
    Node("p")
      .text("For information about the formula language, please take a look into the ")
      .child(Link(Actions.gotoFAQ).text("FAQ"))
      .text(".")

  private def finishButton(
      fractalEntity: FractalEntity
  )(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Button
      .list()
      .child(
        Button("Save Fractal", Icons.save, Actions.saveAsNewFractal(fractalEntity))
          .classes("is-primary")
      )
}
