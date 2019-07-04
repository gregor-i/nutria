package nutria.frontend.shaderBuilder

//sealed
trait WebGlExpression[T <: WebGlType] {
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

case class PureStringExpression[T <: WebGlType](toCode: String) extends WebGlExpression[T]
