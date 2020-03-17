package nutria.frontend.ui

import monocle.{Iso, Lens, PLens}
import nutria.core._
import nutria.frontend._
import nutria.frontend.ui.common.{Form, _}
import nutria.frontend.util.LenseUtils
import snabbdom.Node

object DetailsUi extends Page[DetailsState] {
  def render(implicit state: DetailsState, update: NutriaState => Unit) =
    Body()
      .child(common.Header())
      .child(body(state, update))
      .child(common.Footer())

  def body(implicit state: DetailsState, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text(state.remoteFractal.entity.title))
          .child(Node("h2.subtitle").text("ID: " + state.remoteFractal.id))
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("General Settings:"),
          general()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Template Settings:"),
          template()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Parameter Settings:"),
          parameter()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Snapshots:"),
          snapshots()
        )
      )
      .child(
        Node("section.section")
          .child(actions())
      )

  def general()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val startLens = DetailsState.fractalToEdit composeLens FractalEntityWithId.entity
    Seq(
      Form.stringInput("Title", startLens composeLens FractalEntity.title),
      Form.stringInput("Description", startLens composeLens FractalEntity.description),
      Form.booleanInput("Published", startLens composeLens FractalEntity.published),
      Form.stringInput(
        "References",
        startLens composeLens FractalEntity.reference composeIso Iso[List[String], String](
          _.mkString(" ")
        )(_.split("\\s").filter(_.nonEmpty).toList)
      )
    )
  }

  def template()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val toEditProgram = DetailsState.fractalToEdit composeLens FractalEntityWithId.entity composeLens FractalEntity.program

    val fractal = state.fractalToEdit

    val selectFractalTemplate = Form.selectInput(
      label = "Type",
      options = Vector(
        "Diverging Series"  -> DivergingSeries.default,
        "Newton Iteration"  -> NewtonIteration.default,
        "Freestyle Program" -> FreestyleProgram.default
      ),
      lens = toEditProgram,
      eqFunction = (a: FractalProgram, b: FractalProgram) => a.getClass eq b.getClass
    )

    val additionalParams = fractal.entity.program match {
      case f: DivergingSeries =>
        val lensFractal: Lens[DetailsState, DivergingSeries] = toEditProgram composeLens LenseUtils.lookedUp(
          f,
          FractalProgram.divergingSeries.asSetter
        )

        val selectColoringTemplate = Form.selectInput(
          label = "Coloring",
          options = Vector(
            "Time Escape"    -> TimeEscape(),
            "Normal Map"     -> NormalMap(),
            "Outer Distance" -> OuterDistance()
          ),
          lens = lensFractal composeLens DivergingSeries.coloring,
          eqFunction = (a: DivergingSeriesColoring, b: DivergingSeriesColoring) => a.getClass eq b.getClass
        )
        Seq(selectColoringTemplate)
      case f: FreestyleProgram =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(
          f,
          FractalProgram.freestyleProgram.asSetter
        )
        val templateInput = Form.mulitlineStringInput("template", lensFractal composeLens FreestyleProgram.code)
        Seq(templateInput)
      case _ => Seq.empty
    }

    Seq(selectFractalTemplate) ++ additionalParams
  }

  def parameter()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val startLens     = DetailsState.fractalToEdit composeLens FractalEntityWithId.entity
    val toEditProgram = startLens composeLens FractalEntity.program

    val params = state.fractalToEdit.entity.program match {
      case f: NewtonIteration =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(
          f,
          FractalProgram.newtonIteration.asSetter
        )
        Seq(
          Form.stringFunctionInput("function", lensFractal composeLens NewtonIteration.function),
          Form.stringFunctionInput("initial", lensFractal composeLens NewtonIteration.initial),
          Form.intInput("max iterations", lensFractal composeLens NewtonIteration.maxIterations),
          Form.doubleInput("threshold", lensFractal composeLens NewtonIteration.threshold),
          Form.doubleInput(
            "brightness factor",
            lensFractal composeLens NewtonIteration.brightnessFactor
          ),
          Form.tupleDoubleInput("center", lensFractal composeLens NewtonIteration.center),
          Form.doubleInput("overshoot", lensFractal composeLens NewtonIteration.overshoot)
        )
      case f: DivergingSeries =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(
          f,
          FractalProgram.divergingSeries.asSetter
        )

        val coloringInputs = f.coloring match {
          case coloring: NormalMap =>
            val lensColoring = lensFractal composeLens DivergingSeries.coloring composeLens LenseUtils.lookedUp(
              coloring,
              DivergingSeriesColoring.normalMapColoring.asSetter
            )

            Seq(
              Form.doubleInput("h2", lensColoring composeLens NormalMap.h2),
              Form.doubleInput("angle [0, 2pi]", lensColoring composeLens NormalMap.angle),
              Form.colorInput("color inside", lensColoring composeLens NormalMap.colorInside),
              Form.colorInput("color light", lensColoring composeLens NormalMap.colorLight),
              Form.colorInput("color shadow", lensColoring composeLens NormalMap.colorShadow)
            )
          case coloring: OuterDistance =>
            val lensColoring = lensFractal composeLens DivergingSeries.coloring composeLens LenseUtils.lookedUp(
              coloring,
              DivergingSeriesColoring.outerDistanceColoring.asSetter
            )

            Seq(
              Form.colorInput("color inside", lensColoring composeLens OuterDistance.colorInside),
              Form.colorInput("color near", lensColoring composeLens OuterDistance.colorNear),
              Form.colorInput("color far", lensColoring composeLens OuterDistance.colorFar),
              Form.doubleInput("distance factor", lensColoring composeLens OuterDistance.distanceFactor)
            )
          case coloring: TimeEscape =>
            val lensColoring = lensFractal composeLens DivergingSeries.coloring composeLens LenseUtils.lookedUp(
              coloring,
              DivergingSeriesColoring.timeEscapeColoring.asSetter
            )

            Seq(
              Form.colorInput("color inside", lensColoring composeLens TimeEscape.colorInside),
              Form.colorInput("color outside", lensColoring composeLens TimeEscape.colorOutside)
            )
        }

        Seq(
          Form.stringFunctionInput("initial", lensFractal composeLens DivergingSeries.initial),
          Form.stringFunctionInput("iteration", lensFractal composeLens DivergingSeries.iteration),
          Form.intInput("max iterations", lensFractal composeLens DivergingSeries.maxIterations),
          Form.doubleInput("escape radius", lensFractal composeLens DivergingSeries.escapeRadius)
        ) ++ coloringInputs
      case f: FreestyleProgram =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(
          f,
          FractalProgram.freestyleProgram.asSetter
        )
        f.parameters.indices.map { i =>
          Form.stringInput(
            f.parameters(i).name,
            lensFractal composeLens FreestyleProgram.parameters composeLens LenseUtils
              .seqAt[Parameter](i) composeLens Parameter.literal
          )
        }
    }

    val antiAlias = Form.intInput("Anti Aliasing", startLens composeLens FractalEntity.antiAliase)

    params :+ antiAlias
  }

  def snapshots()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val fractal = state.fractalToEdit.entity

    val tiles = fractal.views.value.map { viewport =>
      val img = FractalImage(fractal.program, viewport, fractal.antiAliase)

      Node("article.fractal-tile.is-relative")
        .child(
          FractalTile(img, Dimensions.thumbnailDimensions)
          // fixme: the viewport should be used in the link
            .event("click", Actions.exploreFractal(state.fractalToEdit))
        )
        .child(
          Node("div.buttons.overlay-bottom-right.padding")
            .child(
              Button
                .icon(Icons.up, Actions.moveViewportUp(viewport))
                .classes("is-outlined")
            )
            .child(
              Button
                .icon(Icons.delete, Actions.deleteViewport(viewport))
                .classes("is-outlined")
            )
        )
    }

    Node("div.fractal-tile-list")
      .child(tiles)
      .child(GalleryUi.dummyTiles)
  }

  private def actions()(implicit state: DetailsState, update: NutriaState => Unit): Node = {
    val buttons: Seq[Node] = state.user match {
      case Some(user) if user.id == state.remoteFractal.owner =>
        Seq(buttonDelete, buttonSaveAsNew, buttonSaveAsOld)

      case _ =>
        Seq(buttonFork)
    }

    Node("div.field.is-grouped.is-grouped-right")
      .child(buttons.map(button => Node("p.control").child(button)))
  }

  private def buttonSaveAsNew(implicit state: DetailsState, update: NutriaState => Unit) =
    Button(
      "Save Changes as new Fractal",
      Icons.save,
      Actions.saveAsNewFractal(state.fractalToEdit.entity)
    ).classes("is-light")

  private def buttonSaveAsOld(implicit state: DetailsState, update: NutriaState => Unit) =
    Button(
      "Apply Changes",
      Icons.save,
      Actions.updateFractal(state.fractalToEdit)
    ).classes("is-primary")

  private def buttonDelete(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Delete", Icons.delete, Actions.deleteFractal(state.fractalToEdit.id))
      .classes("is-danger", "is-light")

  private def buttonFork(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Copy this Fractal", Icons.copy, Actions.saveAsNewFractal(state.fractalToEdit.entity))
      .classes("is-primary")
}
