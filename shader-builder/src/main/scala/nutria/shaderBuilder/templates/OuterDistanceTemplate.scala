package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.languages.Lambda
import nutria.core.{DivergingSeries, OuterDistance}
import nutria.macros.StaticContent
import nutria.shaderBuilder._

private[templates] object OuterDistanceTemplate extends Template[DivergingSeries] {
  override def definitions(v: DivergingSeries): Seq[String] = {
    val c = v.coloring.asInstanceOf[OuterDistance]
    Seq(
      constant("max_iterations", IntLiteral(v.maxIterations.value)),
      constant("escape_radius", FloatLiteral(v.escapeRadius.value)),
      constant("distance_factor", FloatLiteral(c.distanceFactor.value)),
      constant("color_inside", Vec4.fromRGBA(c.colorInside)),
      constant("color_far", Vec4.fromRGBA(c.colorFar)),
      constant("color_near", Vec4.fromRGBA(c.colorNear)),
      function("initial", v.initial.node.optimize(PowerOptimizer.optimizer)),
      function("iteration", v.iteration.node.optimize(PowerOptimizer.optimizer)),
      function("initial_derived", v.initial.node.derive(Lambda).optimize(PowerOptimizer.optimizer)),
      function("iteration_derived", DivergingSeries.deriveIteration(v).optimize(PowerOptimizer.optimizer))
    )
  }

  override def main(v: DivergingSeries): String =
    StaticContent("shader-builder/src/main/glsl/outer_distance.glsl")

}
