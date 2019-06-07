package nutria.frontend.shaderBuilder

sealed trait Ref{
  val name: String
}

case class RefInt(name: String) extends Ref
case class RefFloat(name: String) extends Ref
case class RefVec2(name: String) extends Ref
case class RefVec3(name: String) extends Ref
case class RefVec4(name: String) extends Ref

object Ref{
  def webGlType[R <: Ref](implicit refProps: RefProps[R]): String = refProps.webGlType

  def unit[R <: Ref](implicit refProps: RefProps[R]): String = refProps.unit

  trait RefProps[R <: Ref]{
    val unit: String
    val webGlType: String
    def construct(name: String): R
  }

  implicit object RefPropsInt extends RefProps[RefInt]{
    override val unit: String = "0"
    override val webGlType: String = "int"
    override def construct(name: String): RefInt = RefInt(name)
  }

  implicit object RefPropsVec2 extends RefProps[RefVec2]{
    override val unit: String = "vec2(0.0, 0.0)"
    override val webGlType: String = "vec2"
    override def construct(name: String): RefVec2 = RefVec2(name)
  }

  implicit object RefPropsVec4 extends RefProps[RefVec4]{
    override val unit: String = "vec4(0.0, 0.0, 0.0, 0.0)"
    override val webGlType: String = "vec4"
    override def construct(name: String): RefVec4 = RefVec4(name)
  }


  def declare[R <: Ref : RefProps](ref: Ref): String = s"${webGlType[R]} ${ref.name} = ${unit[R]};"

  def construct[R <: Ref : RefProps](name: String): R = implicitly[RefProps[R]].construct(name)
}

