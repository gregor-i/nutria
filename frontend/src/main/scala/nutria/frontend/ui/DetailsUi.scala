package nutria.frontend.ui

import monocle.Iso
import nutria.core._
import nutria.frontend._
import nutria.frontend.ui.common.{Form, _}
import nutria.frontend.util.LenseUtils
import snabbdom.Node

import scala.util.chaining._
object DetailsUi extends Page[DetailsState] {
  def render(implicit state: DetailsState, update: NutriaState => Unit) =
    Body()
      .child(common.Header())
      .childOptional(
        if (state.dirty)
          Header
            .fab(Node("button"))
            .child(Icons.icon(Icons.save))
            .event("click", Actions.updateFractal(state.fractalToEdit))
            .pipe(Some.apply)
        else None
      )
      .child(body(state, update))
      .child(common.Footer())

  def body(implicit state: DetailsState, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text(Option(state.remoteFractal.entity.title).filter(_.nonEmpty).getOrElse("<No Title given>")))
          .child(Node("h2.subtitle").text(state.remoteFractal.entity.description))
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Administration Attributes:"),
          general()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Primary Attributes:"),
          template()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Secondary Attributes:"),
          parameter()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Saved Snapshots:"),
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
      Form.readonlyStringInput("Published", state.fractalToEdit.entity.published.toString),
      Form.stringInput(
        "References",
        startLens composeLens FractalEntity.reference composeIso Iso[List[String], String](
          _.mkString(" ")
        )(_.split("\\s").filter(_.nonEmpty).toList)
      )
    )
  }

  def template()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val toEditProgram = DetailsState.fractalToEdit
      .composeLens(FractalEntityWithId.entity)
      .composeLens(FractalEntity.program)

    val program = toEditProgram.get(state)

    val fractalType = Form.readonlyStringInput(
      label = "Type",
      value = program match {
        case _: DivergingSeries  => "Diverging Series"
        case _: NewtonIteration  => "Newton Iteration"
        case _: FreestyleProgram => "Freestyle Program"
      }
    )

    val additionalParams = program match {
      case f: DivergingSeries =>
        val lensFractal =
          LenseUtils.subclass(toEditProgram, FractalProgram.divergingSeries, f)

        val selectColoringTemplate = Form.selectInput(
          label = "Coloring",
          options = Vector(
            "Time Escape"    -> TimeEscape(),
            "Normal Map"     -> NormalMap(),
            "Outer Distance" -> OuterDistance()
          ),
          lens = lensFractal.composeLens(DivergingSeries.coloring),
          eqFunction = (a: DivergingSeriesColoring, b: DivergingSeriesColoring) => a.getClass eq b.getClass
        )
        Seq(
          Form.stringFunctionInput("initial", lensFractal.composeLens(DivergingSeries.initial)),
          Form.stringFunctionInput("iteration", lensFractal.composeLens(DivergingSeries.iteration)),
          selectColoringTemplate
        )
      case f: FreestyleProgram =>
        val lensFractal = LenseUtils.subclass(toEditProgram, FractalProgram.freestyleProgram, f)

        val templateInput = Form.mulitlineStringInput("template", lensFractal composeLens FreestyleProgram.code)
        Seq(templateInput)
      case f: NewtonIteration =>
        val lensFractal = LenseUtils.subclass(toEditProgram, FractalProgram.newtonIteration, f)
        Seq(
          Form.stringFunctionInput("function", lensFractal.composeLens(NewtonIteration.function)),
          Form.stringFunctionInput("initial", lensFractal.composeLens(NewtonIteration.initial))
        )
    }

    Seq(fractalType) ++ additionalParams
  }

  def parameter()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val toEditProgram = DetailsState.fractalToEdit
      .composeLens(FractalEntityWithId.entity)
      .composeLens(FractalEntity.program)

    val params = state.fractalToEdit.entity.program match {
      case f: NewtonIteration =>
        val lensFractal = LenseUtils.subclass(toEditProgram, FractalProgram.newtonIteration, f)
        Seq(
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
        val lensFractal = LenseUtils.subclass(toEditProgram, FractalProgram.divergingSeries, f)

        val coloringInputs = f.coloring match {
          case coloring: NormalMap =>
            val lensColoring = lensFractal
              .composeLens(DivergingSeries.coloring)
              .pipe(LenseUtils.subclass(_, DivergingSeriesColoring.normalMapColoring, coloring))

            Seq(
              Form.doubleInput("h2", lensColoring composeLens NormalMap.h2),
              Form.doubleInput("angle [0, 2pi]", lensColoring composeLens NormalMap.angle),
              Form.colorInput("color inside", lensColoring composeLens NormalMap.colorInside),
              Form.colorInput("color light", lensColoring composeLens NormalMap.colorLight),
              Form.colorInput("color shadow", lensColoring composeLens NormalMap.colorShadow)
            )
          case coloring: OuterDistance =>
            val lensColoring = lensFractal
              .composeLens(DivergingSeries.coloring)
              .pipe(LenseUtils.subclass(_, DivergingSeriesColoring.outerDistanceColoring, coloring))

            Seq(
              Form.colorInput("color inside", lensColoring composeLens OuterDistance.colorInside),
              Form.colorInput("color near", lensColoring composeLens OuterDistance.colorNear),
              Form.colorInput("color far", lensColoring composeLens OuterDistance.colorFar),
              Form.doubleInput("distance factor", lensColoring composeLens OuterDistance.distanceFactor)
            )
          case coloring: TimeEscape =>
            val lensColoring = lensFractal
              .composeLens(DivergingSeries.coloring)
              .pipe(LenseUtils.subclass(_, DivergingSeriesColoring.timeEscapeColoring, coloring))

            Seq(
              Form.colorInput("color inside", lensColoring composeLens TimeEscape.colorInside),
              Form.colorInput("color outside", lensColoring composeLens TimeEscape.colorOutside)
            )
        }

        Seq(
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

    val antiAlias =
      Form.intInput("Anti Aliasing", DetailsState.fractalToEdit composeLens FractalEntityWithId.entity composeLens FractalEntity.antiAliase)

    params :+ antiAlias
  }

  def snapshots()(implicit state: DetailsState, update: NutriaState => Unit) = {
    val fractal = state.fractalToEdit.entity

    val tiles = fractal.views.value.map { viewport =>
      val img = FractalImage(fractal.program, viewport, fractal.antiAliase)

      Node("article.fractal-tile.is-relative")
        .child(
          FractalTile(img, Dimensions.thumbnail)
            .event("click", Actions.exploreFractal(state.fractalToEdit, img))
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
        Seq(buttonDelete, buttonSaveAsOld)

      case _ =>
        Seq(buttonFork)
    }

    Node("div.field.is-grouped.is-grouped-right")
      .child(buttons.map(button => Node("p.control").child(button)))
  }

  private def buttonSaveAsOld(implicit state: DetailsState, update: NutriaState => Unit) =
    Button(
      "Save Changes",
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
