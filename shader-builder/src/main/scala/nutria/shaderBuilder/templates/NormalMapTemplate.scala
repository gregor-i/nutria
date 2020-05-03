package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.languages.Lambda
import nutria.core.{DivergingSeries, NormalMap}
import nutria.macros.StaticContent
import nutria.shaderBuilder._

private[templates] object NormalMapTemplate extends Template[DivergingSeries] {
  override def definitions(v: DivergingSeries): Seq[String] = {
    val c = v.coloring.asInstanceOf[NormalMap]
    Seq(
      constant("max_iterations", IntLiteral(v.maxIterations.value)),
      constant("escape_radius", FloatLiteral(v.escapeRadius.value)),
      constant("angle", FloatLiteral(c.angle.value)),
      constant("color_inside", Vec4.fromRGBA(c.colorInside)),
      constant("color_light", Vec4.fromRGBA(c.colorLight)),
      constant("color_shadow", Vec4.fromRGBA(c.colorShadow)),
      constant("h2", FloatLiteral(c.h2.value)),
      function("initial", v.initial.node.optimize(PowerOptimizer.optimizer)),
      function("iteration", v.iteration.node.optimize(PowerOptimizer.optimizer)),
      function("initial_derived", v.initial.node.derive(Lambda).optimize(PowerOptimizer.optimizer)),
      function("iteration_derived", DivergingSeries.deriveIteration(v).optimize(PowerOptimizer.optimizer))
    )
  }

  override def main(v: DivergingSeries): String =
    StaticContent("shader-builder/src/main/glsl/normal_map.glsl")

}
