package nutria.frontend.common

import com.raquo.snabbdom.simple.implicits._
import com.raquo.snabbdom.simple.{VNode, attrs, tags}
import monocle.Lens
import nutria.core._
import nutria.frontend.LenseUtils
import nutria.frontend.library.LibraryUi.seqNode
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Hooks
import nutria.frontend.viewer.Viewer
import org.scalajs.dom.html.Canvas

object RenderEditFractalEntity {
  def apply[A](fractal: FractalEntity, lens: Lens[A, FractalEntity])
              (implicit state: A, update: A => Unit): VNode = {
    tags.build("article")(
      tags.a(
        attrs.href := Viewer.url(fractal),
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 225,
          Hooks.insertHook { node =>
            val canvas = node.elm.get.asInstanceOf[Canvas]
            FractalRenderer.render(canvas, fractal.program, false)
          },
          Hooks.postPatchHook { (_, newNode) =>
            val canvas = newNode.elm.get.asInstanceOf[Canvas]
            FractalRenderer.render(canvas, fractal.program, false)
          }
        )
      ),
      tags.form(
        Form.selectInput(
          label = "Fractal Type",
          options = Vector(
            "Mandelbrot",
            "JuliaSet",
            //            "TricornIteration",
            "NewtonIteration"
          ),
          value = fractal.program.getClass.getSimpleName,
          onChange = {
            case "Mandelbrot" => update((lens composeLens FractalEntity.program).set(Mandelbrot())(state))
            case "JuliaSet" => update((lens composeLens FractalEntity.program).set(JuliaSet())(state))
            //            case "TricornIteration" => update((lensEdit composeLens FractalEntity.program).set(TricornIteration())(state))
            case "NewtonIteration" => update((lens composeLens FractalEntity.program).set(NewtonIteration())(state))
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
          case f: Mandelbrot =>
            val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.mandelbrot.asSetter)
            Seq(
              Form.intInput("max iterations", lensFractal composeLens Mandelbrot.maxIterations),
              Form.intInput("anti aliase", lensFractal composeLens Mandelbrot.antiAliase),
              Form.doubleInput("escape radius", lensFractal composeLens Mandelbrot.escapeRadius),
              Form.booleanInput("shaded", lensFractal composeLens Mandelbrot.shaded),
            )
          case f: JuliaSet =>
            val lensFractal = lens composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.juliaSet.asSetter)
            Seq(
              Form.tupleDoubleInput("c", lensFractal composeLens JuliaSet.c),
              Form.intInput("max iterations", lensFractal composeLens JuliaSet.maxIterations),
              Form.intInput("anti aliase", lensFractal composeLens JuliaSet.antiAliase),
              Form.doubleInput("escape radius", lensFractal composeLens JuliaSet.escapeRadius),
              Form.booleanInput("shaded", lensFractal composeLens JuliaSet.shaded),
            )
        }),
        Form.stringInput("description", lens composeLens FractalEntity.description),
        Form.stringInput("reference", lens composeLens LenseUtils.withDefault(FractalEntity.reference, "")),
      )
    )
  }
}