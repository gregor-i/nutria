package nutria.frontend.ui

import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.NonEmpty
import monocle.{Iso, Lens}
import nutria.core._
import nutria.frontend._
import nutria.frontend.ui.common._
import nutria.frontend.util.LenseUtils
import org.scalajs.dom
import snabbdom.{Node, Snabbdom, VNode}

import scala.concurrent.ExecutionContext.Implicits.global

object DetailsUi {
  def render(implicit state: DetailsState, update: NutriaState => Unit) =
    Node("body")
      .key("explorer")
      .children(
        common.Header(state, update),
        body(state, update),
        common.Footer()
      ).toVNode

  def body(implicit state: DetailsState, update: NutriaState => Unit) =
    Node("div.container")
      .children(
        Node("h1.title.is-1").text(state.remoteFractal.entity.title).toVNode,
        Node("h2.subtitle.is-").text("ID: " + state.remoteFractal.id).toVNode,
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("General Settings:").toVNode,
          general()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Template Settings:").toVNode,
          template()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Parameter Settings:").toVNode,
          parameter()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Snapshots:").toVNode,
          snapshots()
        )
      )
      .child(
        Node("section.section")
          .child(actions())
      )
      .toVNode

  def general()
             (implicit state: DetailsState, update: NutriaState => Unit) = {
    val startLens = DetailsState.fractalToEdit composeLens FractalEntityWithId.entity
    Seq(
      Form.stringInput("Title", startLens composeLens FractalEntity.title),
      Form.stringInput("Description", startLens composeLens FractalEntity.description),
      Form.booleanInput("Published", startLens composeLens FractalEntity.published),
      Form.stringInput("References", startLens composeLens FractalEntity.reference composeIso Iso[List[String], String](_.mkString(" "))(_.split("\\s").filter(_.nonEmpty).toList)),
    )
  }

  def template()
              (implicit state: DetailsState, update: NutriaState => Unit) = {
    val toEditProgram = DetailsState.fractalToEdit composeLens FractalEntityWithId.entity composeLens FractalEntity.program

    val fractal = state.fractalToEdit

    val selectFractalTemplate = Form.selectInput(
      label = "Type",
      options = Vector(
        "NewtonIteration",
        "DivergingSeries",
        "DerivedDivergingSeries",
        "FreestyleProgram"
      ),
      value = fractal.entity.program.getClass.getSimpleName,
      onChange = {
        case "NewtonIteration" => update(toEditProgram.set(NewtonIteration.default)(state))
        case "DivergingSeries" => update(toEditProgram.set(DivergingSeries.default)(state))
        case "DerivedDivergingSeries" => update(toEditProgram.set(DerivedDivergingSeries.default)(state))
        case "FreestyleProgram" => update(toEditProgram.set(FreestyleProgram.default)(state))
      }
    )

    val freeStyleParamters = fractal.entity.program match {
      case f: FreestyleProgram =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(f, FractalProgram.freestyleProgram.asSetter)
        Seq(
          Form.mulitlineStringInput("template", lensFractal composeLens FreestyleProgram.code),
        )
      case _ => Seq.empty
    }

    Seq(selectFractalTemplate) ++ freeStyleParamters
  }

  def parameter()
               (implicit state: DetailsState, update: NutriaState => Unit) = {
    val startLens = DetailsState.fractalToEdit composeLens FractalEntityWithId.entity
    val toEditProgram = startLens composeLens FractalEntity.program

    val params = state.fractalToEdit.entity.program match {
      case f: NewtonIteration =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(f, FractalProgram.newtonIteration.asSetter)
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
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(f, FractalProgram.derivedDivergingSeries.asSetter)
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
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(f, FractalProgram.divergingSeries.asSetter)
        Seq(
          Form.stringFunctionInput("initial", lensFractal composeLens DivergingSeries.initial),
          Form.stringFunctionInput("iteration", lensFractal composeLens DivergingSeries.iteration),
          Form.intInput("max iterations", lensFractal composeLens DivergingSeries.maxIterations),
          Form.doubleInput("escape radius", lensFractal composeLens DivergingSeries.escapeRadius),
          Form.colorInput("color inside", lensFractal composeLens DivergingSeries.colorInside),
          Form.colorInput("color outside", lensFractal composeLens DivergingSeries.colorOutside),
        )
      case f: FreestyleProgram =>
        val lensFractal = toEditProgram composeLens LenseUtils.lookedUp(f, FractalProgram.freestyleProgram.asSetter)
        f.parameters.indices.map {
          i =>
            Form.stringInput(f.parameters(i).name, lensFractal composeLens FreestyleProgram.parameters composeLens LenseUtils.seqAt[Parameter](i) composeLens Parameter.literal)
        }
    }

    val antiAlias = Form.intInput("Anti Aliasing", startLens composeLens FractalEntity.antiAliase)

    params :+ antiAlias
  }

  def snapshots()
               (implicit state: DetailsState, update: NutriaState => Unit) = {
    val fractalId = if (state.dirty) None else Some(state.remoteFractal.id)
    val owned = state.user.exists(_.id == state.remoteFractal.owner)

    val fractal = state.fractalToEdit.entity

    val lensViewports = DetailsState.fractalToEdit.composeLens(FractalEntityWithId.entity)
        .composeLens(FractalEntity.views)

    val tiles = fractal.views.value.map { viewport =>
      val img = FractalImage(fractal.program, viewport, fractal.antiAliase)

      Node("article.fractal-tile")
        .classes("is-relative")
        .child(
          FractalTile(img, Dimensions.thumbnailDimensions)
            .event("click", Snabbdom.event { _ =>
              update(ExplorerState(state.user, fractalId, owned = owned, img))
            })
        )
        .child(
          Node("div.buttons")
            .style("position", "absolute")
            .style("right", "4px")
            .style("top", "4px")
            .child(
              Button.icon(Icons.up, Snabbdom.event { _ =>
                val newViewports = fractal.views.value.filter(_ == viewport) ++ fractal.views.value.filter(_ != viewport)
                refineV[NonEmpty](newViewports) match {
                  case Right(newViews) => update(lensViewports.set(newViews)(state))
                  case Left(_) => ???
                }
              })
                .classes("is-outlined")
            )
            .child(
              Button.icon(Icons.delete, Snabbdom.event { _ =>
                val newViewports = fractal.views.value.filter(_ != viewport)
                refineV[NonEmpty](newViewports) match {
                  case Right(newViews) => update(lensViewports.set(newViews)(state))
                  case Left(_) => dom.window.alert("the last snapshot can't be deleted.")
                }
              })
                .classes("is-danger", "is-outlined")
            )
        )
        .toVNode
    }

    Node("div.fractal-tile-list")
      .child(tiles)
      .child(LibraryUi.dummyTiles)
      .toVNode
  }

  private def actions()(implicit state: DetailsState, update: NutriaState => Unit): VNode = {
    val buttons: Seq[Node] = state.user match {
      case Some(user) if user.id == state.remoteFractal.owner =>
        Seq(buttonDelete, buttonSaveAsNew, buttonSaveAsOld)

      case Some(_) =>
        Seq(buttonFork)

      case None =>
        Seq(buttonLogin)
    }

    Node("div.field.is-grouped.is-grouped-right")
      .child(buttons.map(button => Node("p.control").child(button).toVNode))
      .toVNode
  }

  private def buttonSaveAsNew(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Save Changes as new Fractal", Icons.save, Snabbdom.event { _ =>
      (for {
        fractalWithId <- NutriaService.save(state.fractalToEdit.entity)
      } yield state.copy(remoteFractal = fractalWithId, fractalToEdit = fractalWithId))
        .foreach(update)
    })
      .classes("is-light")

  private def buttonSaveAsOld(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Apply Changes", Icons.save, Snabbdom.event { _ =>
      val updatedFractal = state.fractalToEdit.copy(id = state.remoteFractal.id)
      (for {
        _ <- NutriaService.updateUserFractal(updatedFractal)
      } yield state.copy(remoteFractal = updatedFractal, fractalToEdit = updatedFractal))
        .foreach(update)
    })
      .classes("is-primary")

  private def buttonDelete(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Delete", Icons.delete, Snabbdom.event { _ =>
      (for {
        _ <- NutriaService.deleteUserFractal(state.user.get.id, state.remoteFractal.id)
        publicFractals <- NutriaService.loadPublicFractals()
      } yield LibraryState(user = state.user, publicFractals = publicFractals))
        .foreach(update)
    })
      .classes("is-danger", "is-light")

  private def buttonFork(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Fork", Icons.copy, Snabbdom.event { _ =>
      (for {
        fractalWithId <- NutriaService.save(state.fractalToEdit.entity)
      } yield state.copy(remoteFractal = fractalWithId, fractalToEdit = fractalWithId))
        .foreach(update)
    })
      .classes("is-primary")

  private def buttonLogin(implicit state: DetailsState, update: NutriaState => Unit) =
    Button("Login to update this fractal", "sign-in", Snabbdom.event(_ => dom.window.location.href = Header.loginHref))
      .classes("is-primary")
}
