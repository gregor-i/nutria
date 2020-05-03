package nutria.shaderBuilder.templates

import mathParser.Syntax._
import nutria.core.{DivergingSeries, TimeEscape}
import nutria.macros.StaticContent
import nutria.shaderBuilder._

private[templates] object TimeEscapeTemplate extends Template[DivergingSeries] {
  override def definitions(v: DivergingSeries): Seq[String] = {
    val c = v.coloring.asInstanceOf[TimeEscape]
    Seq(
      constant("max_iterations", IntLiteral(v.maxIterations.value)),
      constant("escape_radius", FloatLiteral(v.escapeRadius.value)),
      constant("color_inside", Vec4.fromRGBA(c.colorInside)),
      constant("color_outside", Vec4.fromRGBA(c.colorOutside)),
      function("initial", v.initial.node.optimize(PowerOptimizer.optimizer)),
      function("iteration", v.iteration.node.optimize(PowerOptimizer.optimizer))
    )
  }

  override def main(v: DivergingSeries): String =
    StaticContent("shader-builder/src/main/glsl/time_escape.glsl")

}
