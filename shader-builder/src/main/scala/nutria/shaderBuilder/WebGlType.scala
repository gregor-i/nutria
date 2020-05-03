package nutria.shaderBuilder

sealed trait WebGlType
case object WebGlTypeInt   extends WebGlType
case object WebGlTypeFloat extends WebGlType
case object WebGlTypeVec3  extends WebGlType
case object WebGlTypeVec2  extends WebGlType
case object WebGlTypeVec4  extends WebGlType

object WebGlType {
  def declare[T <: WebGlType: TypeProps](ref: Ref[T], expr: WebGlExpression[T]): String =
    s"${TypeProps[T].webGlType} ${assign(ref, expr)}"

  def assign[T <: WebGlType: TypeProps](ref: Ref[T], expr: WebGlExpression[T]): String =
    s"${ref.name} = ${expr.toCode};"

  def zero[T <: WebGlType: TypeProps]: WebGlExpression[T] = TypeProps[T].zero

  def reference[T <: WebGlType: TypeProps](name: String): Ref[T] = TypeProps[T].construct(name)

  trait TypeProps[T <: WebGlType] {
    val zero: WebGlExpression[T]
    val webGlType: String

    def construct(name: String): Ref[T]
  }

  object TypeProps {
    def apply[T <: WebGlType](implicit props: TypeProps[T]): TypeProps[T] = props
  }

  implicit object TypePropsInt extends TypeProps[WebGlTypeInt.type] {
    override val zero: WebGlExpression[WebGlTypeInt.type] = IntLiteral(0)
    override val webGlType: String                        = "int"

    override def construct(name: String): RefInt = RefInt(name)
  }

  implicit object TypePropsFloat extends TypeProps[WebGlTypeFloat.type] {
    override val zero: WebGlExpression[WebGlTypeFloat.type] = FloatLiteral(0)
    override val webGlType: String                          = "float"

    override def construct(name: String): RefFloat = RefFloat(name)
  }

  implicit object TypePropsVec2 extends TypeProps[WebGlTypeVec2.type] {
    override val zero: WebGlExpression[WebGlTypeVec2.type] = Vec2(FloatLiteral(0), FloatLiteral(0))
    override val webGlType: String                         = "vec2"

    override def construct(name: String): RefVec2 = RefVec2(name)
  }

  implicit object TypePropsVec3 extends TypeProps[WebGlTypeVec3.type] {
    override val zero: WebGlExpression[WebGlTypeVec3.type] = Vec3(FloatLiteral(0), FloatLiteral(0), FloatLiteral(0))
    override val webGlType: String                         = "vec3"

    override def construct(name: String): RefVec3 = RefVec3(name)
  }

  implicit object TypePropsVec4 extends TypeProps[WebGlTypeVec4.type] {
    override val zero: WebGlExpression[WebGlTypeVec4.type] =
      Vec4(FloatLiteral(0), FloatLiteral(0), FloatLiteral(0), FloatLiteral(0))
    override val webGlType: String = "vec4"

    override def construct(name: String): RefVec4 = RefVec4(name)
  }

}
