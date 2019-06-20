package nutria.frontend.shaderBuilder

import mathParser.complex.ComplexLanguage
import nutria.frontend.ViewerUi
import spire.math.Complex

sealed trait Iteration
sealed trait DeriveableIteration

case object MandelbrotIteration extends Iteration with DeriveableIteration
case class JuliaSetIteration(c: Complex[Double]) extends Iteration with DeriveableIteration
case object TricornIteration extends Iteration
case class NewtonIteration(function: String) extends Iteration

object Iteration {
  def toCode(node: ComplexLanguage#Node): String =
    node.fold[String](
      ifConstant = c => Vec2(FloatLiteral(c.real.toFloat), FloatLiteral(c.imag.toFloat)).toCode,
      ifBinary = (op, left, right) => op match {
        case Parser.lang.Plus => left + "+" + right
        case Parser.lang.Minus => left + "-" + right
        case Parser.lang.Times => s"product(vec2($left), vec2($right))"
        case Parser.lang.Divided => s"divide(vec2($left), vec2($right))"
        case Parser.lang.Power =>
          println("power")
          ???
      },
      ifUnitary = (op, child) => ???,
      ifVariable = _ match {
        case 'x => "z"
      }
    )

  def initial(iteration: Iteration)(z: RefVec2, p: RefVec2): String =
    iteration match {
      case _ => s"""
                   |vec2 ${z.name} = ${p.name};
                   |""".stripMargin
      }

  def newtonInitial(f: ComplexLanguage#Node, f_der: ComplexLanguage#Node)(z: RefVec2, p: RefVec2): String = {
    val fz = RefVec2("fz")
    val flast = RefVec2("fzlast")
    s"""
       |${WebGlType.declare(z, RefExp(p))}
       |${WebGlType.declare(fz, PureStringExpression(toCode(f)))}
       |${WebGlType.declare(flast, RefExp(fz))}
      """.
      stripMargin
  }



  def step(iteration: Iteration)(z: RefVec2, p: RefVec2): String =
    iteration match {
      case MandelbrotIteration =>
        s"""
           |${z.name} = product(${z.name}, ${z.name}) + ${p.name};
           |""".stripMargin
      case JuliaSetIteration(c) =>
        s"""
           |${z.name} = product(${z.name}, ${z.name}) + ${Vec2(FloatLiteral(c.real.toFloat), FloatLiteral(c.imag.toFloat)).toCode};
           |""".stripMargin
      case TricornIteration =>
        s"""
           |${z.name} = conjugate(product(${z.name}, ${z.name})) + ${p.name};
           |""".stripMargin

    }

  def newtonStep(f: ComplexLanguage#Node, f_der: ComplexLanguage#Node)(z: RefVec2, p: RefVec2): String = {
    val fz = RefVec2("fz")
    val flast = RefVec2("fzlast")
    val fderz = RefVec2("fderz")
    s"""
       |${WebGlType.assign(flast, RefExp(fz))}
       |${WebGlType.assign(fz, PureStringExpression(toCode(f)))};
       |${WebGlType.declare(fderz, PureStringExpression(toCode(f_der)))}
       |${z.name} -= divide(${fz.name}, ${fderz.name});
         """.stripMargin
  }
}

object DeriveableIteration{
  def initial(iteration: DeriveableIteration)(z: RefVec2, p: RefVec2): String =
    s"""
       |${WebGlType.declare(z, RefExp(p))};
       |vec2 ${z.name}_der = vec2(1.0, 0.0);
     """.stripMargin

  def step(iteration: DeriveableIteration)(z: RefVec2, p: RefVec2): String =
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
