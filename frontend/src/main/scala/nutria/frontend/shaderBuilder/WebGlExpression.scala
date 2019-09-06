package nutria.frontend.shaderBuilder

import mathParser.algebra.{SpireBinaryOperator, SpireUnitaryOperator}

sealed trait WebGlExpression[T <: WebGlType] {
  def toCode: String
}

case class IntLiteral(value: Int) extends WebGlExpression[WebGlTypeInt.type] {
  override def toCode: String = value.toString
}

case class FloatLiteral(value: Float) extends WebGlExpression[WebGlTypeFloat.type] {
  override def toCode: String = s"float($value)"
}

case class Vec2(a: WebGlExpression[WebGlTypeFloat.type],
                b: WebGlExpression[WebGlTypeFloat.type]) extends WebGlExpression[WebGlTypeVec2.type] {
  override def toCode: String = s"vec2(${a.toCode}, ${b.toCode})"
}

case class Vec3(a: WebGlExpression[WebGlTypeFloat.type],
                b: WebGlExpression[WebGlTypeFloat.type],
                c: WebGlExpression[WebGlTypeFloat.type]) extends WebGlExpression[WebGlTypeVec3.type] {
  override def toCode: String = s"vec3(${a.toCode}, ${b.toCode}, ${c.toCode})"
}

case class Vec4(a: WebGlExpression[WebGlTypeFloat.type],
                b: WebGlExpression[WebGlTypeFloat.type],
                c: WebGlExpression[WebGlTypeFloat.type],
                d: WebGlExpression[WebGlTypeFloat.type]) extends WebGlExpression[WebGlTypeVec4.type] {
  override def toCode: String = s"vec4(${a.toCode}, ${b.toCode}, ${c.toCode}, ${d.toCode})"
}

case class RefExp[T <: WebGlType](ref: Ref[T]) extends WebGlExpression[T] {
  override def toCode: String = ref.name
}

case class ComplexBinaryExp(op: SpireBinaryOperator,
                            left: WebGlExpression[WebGlTypeVec2.type],
                            right: WebGlExpression[WebGlTypeVec2.type]) extends WebGlExpression[WebGlTypeVec2.type] {
  import mathParser.algebra._
  override def toCode: String = op match {
    case Plus => left.toCode + "+" + right.toCode
    case Minus => left.toCode + "-" + right.toCode
    case Times => s"complex_product(vec2(${left.toCode}), vec2(${right.toCode}))"
    case Divided => s"complex_divide(vec2(${left.toCode}), vec2(${right.toCode}))"
    case Power => s"complex_power(vec2(${left.toCode}), vec2(${right.toCode}))"
  }
}

case class ComplexUnitaryExp(op: SpireUnitaryOperator,
                             child: WebGlExpression[WebGlTypeVec2.type]) extends WebGlExpression[WebGlTypeVec2.type] {
  import mathParser.algebra._
  override def toCode: String = op match {
    case Neg => s"-(${child.toCode})"
    case Sin => s"complex_sin(vec2(${child.toCode}))"
    case Cos => s"complex_cos(vec2(${child.toCode}))"
    case Tan => s"complex_tan(vec2(${child.toCode}))"
    case Asin => ???
    case Acos => ???
    case Atan => ???
    case Sinh => ???
    case Cosh => ???
    case Tanh => ???
    case Exp => s"complex_exp(vec2(${child.toCode}))"
    case Log => s"complex_log(vec2(${child.toCode}))"
  }
}

