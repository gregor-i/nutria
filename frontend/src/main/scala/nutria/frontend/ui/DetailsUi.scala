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
    Node("div.details-body")
      .children(
        Node("h2.title").text("General Settings:").toVNode,
        general(state.fractal, DetailsState.fractalEntity),
        Node("h2.title").text("Template Settings:").toVNode,
        template(state.fractal, DetailsState.fractalEntity),
        Node("h2.title").text("Parameter Settings:").toVNode,
        parameter(state.fractal, DetailsState.fractalEntity),
        Node("h2.title").text("Snapshots:").toVNode,
        snapshots(state.fractal, DetailsState.fractalEntity),
        actions(),
      ).toVNode

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
    val fractalId = if (state.dirty) None else Some(state.remoteFractal.id)
    val owned = state.user.exists(_.id == state.remoteFractal.owner)

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
                  case Right(newViews) => update(state.copy(fractal = fractal.copy(views = newViews)))
                  case Left(_) => ???
                }
              })
                .classes("is-outlined")
            )
            .child(
              Button.icon(Icons.delete, Snabbdom.event { _ =>
                val newViewports = fractal.views.value.filter(_ != viewport)
                refineV[NonEmpty](newViewports) match {
                  case Right(newViews) => update(state.copy(fractal = fractal.copy(views = newViews)))
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
    val fractal = state.fractal

    state.user match {
      case Some(user) if user.id == state.remoteFractal.owner =>
        Node("div.buttons")
          .child(
            Button("Save Changes as new Fractal", Icons.save, Snabbdom.event { _ =>
              val updatedFractal = state.remoteFractal.copy(entity = fractal)
              (for {
                fractalWithId <- NutriaService.save(updatedFractal.entity)
              } yield DetailsState(state.user, fractalWithId, fractalWithId.entity))
                .foreach(update)
            })
              .classes("is-primary")
          )
          .child(
            Button("Apply Changes", Icons.save, Snabbdom.event { _ =>
              val updatedFractal = state.remoteFractal.copy(entity = fractal)
              (for {
                _ <- NutriaService.updateUserFractal(updatedFractal)
              } yield state.copy(remoteFractal = updatedFractal))
                .foreach(update)
            })
              .classes("is-warning")
          )
          .child(
            Button("Delete", Icons.delete, Snabbdom.event { _ =>
              (for {
                _ <- NutriaService.deleteUserFractal(state.user.get.id, state.remoteFractal.id)
                publicFractals <- NutriaService.loadPublicFractals()
              } yield LibraryState(user = state.user, publicFractals = publicFractals))
                .foreach(update)
            })
              .classes("is-danger")
          )
          .toVNode

      case Some(_) =>
        Button("Fork", Icons.copy, Snabbdom.event { _ =>
          (for {
            fractalWithId <- NutriaService.save(fractal)
          } yield DetailsState(state.user, fractalWithId, fractalWithId.entity))
            .foreach(update)
        })
          .classes("is-primary")
          .toVNode

      case None =>
        Button("Login to fork this fractal", "sign-in", Snabbdom.event(_ => ()))
          .classes("is-primary")
          .toVNode
    }
  }
}
