package nutria.frontend.common

import monocle.{Iso, Lens}
import nutria.core._
import nutria.frontend._
import nutria.frontend.util.LenseUtils
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object RenderEditFractalEntity {
  def apply[A](fractal: FractalEntity, currentTab: Tab, lens: Lens[A, FractalEntity], lensTab: Lens[A, Tab], footer: VNode*)
              (implicit state: A, update: A => Unit): VNode = {
    h("div.modal-card")(
      h("header.modal-card-head")(
        h("p.modal-card-title")(
          if (fractal.title.trim.isEmpty) "<no title given>" else fractal.title
        )
      ),
      h("section.modal-card-body", styles = Seq("padding-top" -> "0"))(
        h("div.tabs.is-centered.is-fullwidth")(h("ul")(
          for (tab <- Tab.list) yield h("li",
            classes = Seq("is-active" -> (tab == currentTab))
          )(
            h("a", events = Seq("click" -> snabbdom.Snabbdom.event { _ => update(lensTab.set(tab)(state)) }))(Tab.toString(tab))
          )
        )),
        currentTab match {
          case General => generalBody(fractal, lens)
          case Template => templateBody(fractal, lens)
          case Parameters => parametersBody(fractal, lens)
          case Snapshots => snapshotsBody(fractal, lens)
        }
      ),
      h("footer.modal-card-foot")(
        footer
      )
    )
  }

  private def snapshotsBody[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                              (implicit state: A, update: A => Unit) =
    List(
      h("article.fractal-tile",
        events = Seq("click" -> Snabbdom.event(_ => update(ExplorerState(fractal).asInstanceOf[A]))), // todo: remove hacky cast
      )(FractalImage(fractal, Dimensions.thumbnailDimensions))
    )

  private def parametersBody[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                               (implicit state: A, update: A => Unit) =
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

  private def templateBody[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                             (implicit state: A, update: A => Unit) =
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


  private def generalBody[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                            (implicit state: A, update: A => Unit) =
    Seq(
      Form.stringInput("Title", lens composeLens FractalEntity.title),
      Form.stringInput("Description", lens composeLens FractalEntity.description),
      Form.stringInput("References", lens composeLens FractalEntity.reference composeIso Iso[List[String], String](_.mkString(" "))(_.split("\\s").filter(_.nonEmpty).toList)),
      Form.intInput("Anti Aliasing", lens composeLens FractalEntity.antiAliase),
    )

}
