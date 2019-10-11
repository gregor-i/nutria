package nutria.frontend.common

import monocle.{Iso, Lens}
import nutria.core._
import nutria.frontend.util.LenseUtils
import snabbdom.Snabbdom.h
import snabbdom.VNode

object RenderEditFractalEntity {
  def apply[A](fractal: FractalEntity, lens: Lens[A, FractalEntity], footer: VNode*)
              (implicit state: A, update: A => Unit): VNode = {
    h("div.modal-card")(
      h("header.modal-card-head")(
        header(fractal, lens)
      ),
      h("section.modal-card-body")(
        body(fractal, lens)
      ),
      h("footer.modal-card-foot")(
        footer: _*
      )
    )
  }

  private def header[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                       (implicit state: A, update: A => Unit): VNode =
    h("canvas",
      attrs = Seq(
        "width" -> "400",
        "height" -> "225",
      ),
      styles = Seq(
        "width" -> "100%"
      ),
      hooks = CanvasHooks(fractal, resize = false)
    )()

  private def body[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                     (implicit state: A, update: A => Unit) = {
    val selectFractalType = Form.selectInput(
      label = "Fractal Type",
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

    h("form")(
      (Seq(selectFractalType) ++
        specificInputs(fractal, lens) ++
        Seq(
          Form.intInput("anti aliasing", lens composeLens FractalEntity.antiAliase),
          Form.stringInput("description", lens composeLens FractalEntity.description),
          Form.stringInput("reference", lens composeLens FractalEntity.reference composeIso Iso[List[String], String](_.mkString(" "))(_.split("\\s").filter(_.nonEmpty).toList))
        )): _*
    )
  }

  private def specificInputs[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
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
        Seq(
          Form.mulitlineStringInput("code", lensFractal composeLens FreestyleProgram.code),
        ) ++ f.parameters.indices.map {
          i =>
            Form.stringInput(f.parameters(i).name, lensFractal composeLens FreestyleProgram.parameters composeLens LenseUtils.seqAt[Parameter](i) composeLens Parameter.literal)
        }
    }
}
