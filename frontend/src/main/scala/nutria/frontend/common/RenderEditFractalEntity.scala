package nutria.frontend.common

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple.{VNode, attrs, styles, tags}
import monocle.Lens
import nutria.core._
import nutria.frontend.LenseUtils
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Hooks
import org.scalajs.dom.html.Canvas
import nutria.frontend.util.SnabbdomHelper.seqNode

object RenderEditFractalEntity {
  def apply[A](fractal: FractalEntity, lens: Lens[A, FractalEntity], footer: VNode*)
              (implicit state: A, update: A => Unit): VNode = {
    tags.div(attrs.className := "modal-card",
      tags.build("header")(
        attrs.className := "modal-card-head",
        header(fractal, lens)
      ),
      tags.build("section")(
        attrs.className := "modal-card-body",
        body(fractal, lens)
      ),
      tags.footer(
        attrs.className := "modal-card-foot",
        seqNode(footer)
      )
    )
  }

  private def header[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                       (implicit state: A, update: A => Unit): VNode =
    tags.canvas(
      attrs.widthAttr := 400,
      attrs.heightAttr := 225,
      styles.width := "100%",
      Hooks.insertHook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, false)
      },
      Hooks.postPatchHook { (_, newNode) =>
        val canvas = newNode.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, false)
      }
    )

  private def body[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
                     (implicit state: A, update: A => Unit) =
    tags.form(
      Form.selectInput(
        label = "Fractal Type",
        options = Vector(
          "NewtonIteration",
          "DivergingSeries",
          "DerivedDivergingSeries"
        ),
        value = fractal.program.getClass.getSimpleName,
        onChange = {
          case "NewtonIteration" => update((lens composeLens FractalEntity.program).set(NewtonIteration())(state))
          case "DivergingSeries" => update((lens composeLens FractalEntity.program).set(DivergingSeries.mandelbrot)(state))
          case "DerivedDivergingSeries" => update((lens composeLens FractalEntity.program).set(DerivedDivergingSeries.mandelbrot)(state))
        }
      ),

      seqNode(fractal.program match {
        case f: NewtonIteration =>
          val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.newtonIteration.asSetter)
          Seq(
            Form.stringInput("function", lensFractal composeLens NewtonIteration.function),
            Form.stringInput("initial", lensFractal composeLens NewtonIteration.initial),
            Form.intInput("max iterations", lensFractal composeLens NewtonIteration.maxIterations),
            Form.intInput("anti aliase", lensFractal composeLens NewtonIteration.antiAliase),
            Form.doubleInput("threshold", lensFractal composeLens NewtonIteration.threshold),
            Form.doubleInput("brightness factor", lensFractal composeLens NewtonIteration.brightnessFactor),
            Form.tupleDoubleInput("center", lensFractal composeLens NewtonIteration.center),
            Form.doubleInput("overshoot", lensFractal composeLens NewtonIteration.overshoot),
          )
        case f: DerivedDivergingSeries =>
          val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.derivedDivergingSeries.asSetter)
          Seq(
            Form.intInput("max iterations", lensFractal composeLens DerivedDivergingSeries.maxIterations),
            Form.intInput("anti aliase", lensFractal composeLens DerivedDivergingSeries.antiAliase),
            Form.doubleInput("escape radius", lensFractal composeLens DerivedDivergingSeries.escapeRadius),
            Form.stringInput("initial Z", lensFractal composeLens DerivedDivergingSeries.initialZ),
            Form.stringInput("initial Z'", lensFractal composeLens DerivedDivergingSeries.initialZDer),
            Form.stringInput("iteration Z", lensFractal composeLens DerivedDivergingSeries.iterationZ),
            Form.stringInput("iteration Z'", lensFractal composeLens DerivedDivergingSeries.iterationZDer),
            Form.doubleInput("h2", lensFractal composeLens DerivedDivergingSeries.h2),
            Form.doubleInput("angle [0, 2pi]", lensFractal composeLens DerivedDivergingSeries.angle),
          )
        case f: DivergingSeries =>
          val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.divergingSeries.asSetter)
          Seq(
            Form.stringInput("initial", lensFractal composeLens DivergingSeries.initial),
            Form.stringInput("iteration", lensFractal composeLens DivergingSeries.iteration),
            Form.intInput("max iterations", lensFractal composeLens DivergingSeries.maxIterations),
            Form.intInput("anti aliase", lensFractal composeLens DivergingSeries.antiAliase),
            Form.doubleInput("escape radius", lensFractal composeLens DivergingSeries.escapeRadius),
          )
      }),
      Form.stringInput("description", lens composeLens FractalEntity.description),
      Form.stringInput("reference", lens composeLens LenseUtils.withDefault(FractalEntity.reference, "")),
    )
}
