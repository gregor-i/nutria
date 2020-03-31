package nutria.frontend.ui

import monocle.Lens
import nutria.core.viewport.{Dimensions, Viewport}
import nutria.core.{DivergingSeries, FractalImage, FractalProgram, NewtonIteration}
import nutria.frontend.CreateNewFractalState.{FormulaStep, TypeStep}
import nutria.frontend.ui.common.{Body, Footer, Form, FractalTile, Header, Images, Link}
import nutria.frontend.util.LenseUtils
import nutria.frontend.{Actions, CreateNewFractalState, FAQState, NutriaState}
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
          .child(select())
      )
      .child(Footer())

  private def select()(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    state.step match {
      case TypeStep                             => selectTyp()
      case FormulaStep(series: DivergingSeries) => selectFormulaDivergingSeries(series)
      case FormulaStep(series: NewtonIteration) => selectFormulaNewtonIteration(series)
    }

  private def selectTyp()(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Node("section.section")
      .child(Node("h2.title.is-2").text("Step 1: Select the fractal type"))
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

  private def selectFormulaDivergingSeries(series: DivergingSeries)(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node = {
    val lens: Lens[CreateNewFractalState, DivergingSeries] = CreateNewFractalState.step
      .composePrism(CreateNewFractalState.formulaStep)
      .composeLens(CreateNewFractalState.FormulaStep.program)
      .composePrism(FractalProgram.divergingSeries)
      .asSetter
      .pipe(LenseUtils.lookedUp(series, _))

    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 2: Select the formula"))
      .child(
        Node("div.content")
          .child(divergingSeriesIntroduction)
          .child(languageInformation)
          .child(Node("p").text("Tweek the formulas until you are happy with the result."))
      )
      .child(
        Form.stringFunctionInput("initial(lambda)", lens.composeLens(DivergingSeries.initial))
      )
      .child(
        Form.stringFunctionInput("iteration(z, lambda)", lens.composeLens(DivergingSeries.iteration))
      )
      .child(
        FractalTile(FractalImage(program = series, view = Viewport.mandelbrot), dimensions = Dimensions.thumbnail.scale(1.5))
          .classes("fractal-tile")
          .style("display", "block")
          .style("margin", "8px auto")
      )
      .child(
        continueButtons(
          backState = CreateNewFractalState(state.user),
          continueState = state // todo
        )
      )
  }

  private def selectFormulaNewtonIteration(series: NewtonIteration)(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node = {
    val lens: Lens[CreateNewFractalState, NewtonIteration] = CreateNewFractalState.step
      .composePrism(CreateNewFractalState.formulaStep)
      .composeLens(CreateNewFractalState.FormulaStep.program)
      .composePrism(FractalProgram.newtonIteration)
      .asSetter
      .pipe(LenseUtils.lookedUp(series, _))

    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 2: Select the formula"))
      .child(
        Node("div.content")
          .child(newtonIterationIntroduction)
          .child(languageInformation)
          .child(Node("p").text("Tweek the formulas until you are happy with the result."))
      )
      .child(
        Form.stringFunctionInput("initial(lambda)", lens.composeLens(NewtonIteration.initial))
      )
      .child(
        Form.stringFunctionInput("iteration(z, lambda)", lens.composeLens(NewtonIteration.function))
      )
      .child(
        FractalTile(FractalImage(program = series, view = Viewport.aroundZero), dimensions = Dimensions.thumbnail.scale(1.5))
          .classes("fractal-tile")
          .style("display", "block")
          .style("margin", "8px auto")
      )
      .child(
        continueButtons(
          backState = CreateNewFractalState(state.user),
          continueState = state // todo
        )
      )
  }

  // todo: maybe move to a static html file
  private val divergingSeriesIntroduction =
    Node("p")
      .text("Fractals are described with the use of two mathematical formulas. ")
      .text("The first formula defines how to start the iteration. It is named ")
      .child(Node("code").text("initial"))
      .text(".")
      .text("The second formula defined how to continue the iteration. It is named ")
      .child(Node("code").text("initial"))
      .text(".")
      .text("For example is the famous Mandelbrot fractal created with ")
      .child(Node("code").text("initial(lambda) = 0"))
      .text(" and ")
      .child(Node("code").text("iteration(z, lambda) = z^2 + lambda"))
      .text(".")

  // todo: maybe move to a static html file
  private val newtonIterationIntroduction =
    Node("p")
      .text("Newton Fractals are described with the use of two mathematical formulas. ")
      .text("The first formula defines how to start the iteration. It is named ")
      .child(Node("code").text("initial"))
      .text(".")
      .text("The second formula defined how to continue the iteration. It is named ")
      .child(Node("code").text("initial"))
      .text(".")
      .text("Newton Fractals are based on Newton's Method. ")
      .text("It is an root finding algorithm and Newton Fractals get interesting when Newton's Method fails. ")
      .text("More roots to find make the fractal more interesting. ")

  private def languageInformation(implicit nutriaState: NutriaState, update: NutriaState => Unit) =
    Node("p")
      .text("For information about the formula language, please take a look into the ")
      .child(Link(Actions.gotoFAQ).text("FAQ"))
      .text(".")

  private def continueButtons(
      backState: NutriaState,
      continueState: NutriaState
  )(implicit state: CreateNewFractalState, update: NutriaState => Unit): Node =
    Node("div")
      .classes("is-right", "buttons")
      .child(
        Link(backState)
          .classes("button", "is-secondary")
          .text("Back")
      )
      .child(
        Link(continueState)
          .classes("button", "is-primary")
          .text("Continue")
      )
}
