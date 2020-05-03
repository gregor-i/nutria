package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.NewtonIteration
import nutria.core.languages.X
import nutria.macros.StaticContent
import nutria.shaderBuilder.{FloatLiteral, IntLiteral, RefVec2, RefVec4}

object NewtonIterationTemplate extends Template[NewtonIteration] {
  override def definitions(v: NewtonIteration): Seq[String] = Seq(
    constant("threshold", FloatLiteral(v.threshold.value)),
    constant("overshoot", FloatLiteral(v.overshoot.value)),
    constant("brightness_factor", FloatLiteral(v.brightnessFactor.value)),
    constant("center_x", FloatLiteral(v.center._1)),
    constant("center_y", FloatLiteral(v.center._2)),
    constant("max_iterations", IntLiteral(v.maxIterations.value)),
    function("initial", v.initial.node),
    function("f", v.function.node),
    function("f_derived", v.function.node.derive(X))
  )

  override def main(n: NewtonIteration): String =
    StaticContent("shader-builder/src/main/glsl/newton_iteration.glsl")
}
