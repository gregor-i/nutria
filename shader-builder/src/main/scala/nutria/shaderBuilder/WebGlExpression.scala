package nutria.shaderBuilder

import nutria.core.{RGB, RGBA}

sealed trait WebGlExpression[T <: WebGlType] {
  def toCode: String
}

case class IntLiteral(value: Int) extends WebGlExpression[WebGlTypeInt.type] {
  override def toCode: String = value.toString
}

case class FloatLiteral(value: Float) extends WebGlExpression[WebGlTypeFloat.type] {
  override def toCode: String = if (value == value.toInt.toFloat) s"${value.toInt}.0" else value.toString
}

object FloatLiteral {
  def apply(d: Double): FloatLiteral = FloatLiteral(d.toFloat)
}

case class Vec2(a: WebGlExpression[WebGlTypeFloat.type], b: WebGlExpression[WebGlTypeFloat.type]) extends WebGlExpression[WebGlTypeVec2.type] {
  override def toCode: String = s"vec2(${a.toCode}, ${b.toCode})"
}

case class Vec3(
    a: WebGlExpression[WebGlTypeFloat.type],
    b: WebGlExpression[WebGlTypeFloat.type],
    c: WebGlExpression[WebGlTypeFloat.type]
) extends WebGlExpression[WebGlTypeVec3.type] {
  override def toCode: String = s"vec3(${a.toCode}, ${b.toCode}, ${c.toCode})"
}

case class Vec4(
    a: WebGlExpression[WebGlTypeFloat.type],
    b: WebGlExpression[WebGlTypeFloat.type],
    c: WebGlExpression[WebGlTypeFloat.type],
    d: WebGlExpression[WebGlTypeFloat.type]
) extends WebGlExpression[WebGlTypeVec4.type] {
  override def toCode: String = s"vec4(${a.toCode}, ${b.toCode}, ${c.toCode}, ${d.toCode})"
}

case class RefExp[T <: WebGlType](ref: Ref[T]) extends WebGlExpression[T] {
  override def toCode: String = ref.name
}

object Vec3 {
  def fromRGB(rgb: RGB): Vec3 =
    Vec3(
      FloatLiteral((rgb.R / 255d).toFloat),
      FloatLiteral((rgb.G / 255d).toFloat),
      FloatLiteral((rgb.B / 255d).toFloat)
    )
}

object Vec4 {
  def fromRGBA(rgba: RGBA): Vec4 =
    Vec4(
      FloatLiteral((rgba.R / 255d).toFloat),
      FloatLiteral((rgba.G / 255d).toFloat),
      FloatLiteral((rgba.B / 255d).toFloat),
      FloatLiteral(rgba.A.toFloat)
    )
}
