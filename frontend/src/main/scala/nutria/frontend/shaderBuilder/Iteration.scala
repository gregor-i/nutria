package nutria.frontend.shaderBuilder

import spire.math.Complex

sealed trait Iteration

case object MandelbrotIteration extends Iteration
case class JuliaSetIteration(c: Complex[Double]) extends Iteration

object Iteration{
  def initial(iteration: Iteration)(z: RefVec2, p: RefVec2): String =
    iteration match {
      case MandelbrotIteration =>
        s"""
           |vec2 ${z.name} = ${p.name};
           |""".stripMargin
      case JuliaSetIteration(_) =>
        s"""
           |vec2 ${z.name} = ${p.name};
           |""".stripMargin
    }

  def step(iteration: Iteration)(z: RefVec2, p: RefVec2): String =
    iteration match {
      case MandelbrotIteration =>
        s"""
           |${z.name} = product(${z.name}, ${z.name}) + ${p.name};
           |""".stripMargin
      case JuliaSetIteration(c) =>
        s"""
           |${z.name} = product(${z.name}, ${z.name}) + vec2(float(${c.real}), float(${c.imag}));
           |""".stripMargin
      }


  def initialWithDer(iteration: Iteration)(z: RefVec2, p: RefVec2): String =
    s"""
       |vec2 ${z.name} = ${p.name};
       |vec2 ${z.name}_der = vec2(1.0, 0.0);
     """.stripMargin

  def stepWithDer(iteration: Iteration)(z: RefVec2, p: RefVec2): String =
    iteration match {
      case MandelbrotIteration =>
        s"""
           |vec2 ${z.name}_new = product(${z.name}, ${z.name}) + ${p.name};
           |vec2 ${z.name}_der_new = product(${z.name}_der, z) * 2.0 + vec2(1.0, 0.0);
           |${z.name} = ${z.name}_new;
           |${z.name}_der = ${z.name}_der_new;
           |""".stripMargin
      case JuliaSetIteration(c) =>
        s"""
           |vec2 ${z.name}_new = product(${z.name}, ${z.name}) + vec2(float(${c.real}), float(${c.imag}));
           |vec2 ${z.name}_der_new = product(${z.name}_der, z) * 2.0 + vec2(1.0, 0.0);
           |${z.name} = ${z.name}_new;
           |${z.name}_der = ${z.name}_der_new;
           |""".stripMargin
    }
}