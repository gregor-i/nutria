package nutria.frontend

package object shaderBuilder {
  type RefInt   = Ref[WebGlTypeInt.type]
  type RefFloat = Ref[WebGlTypeFloat.type]
  type RefVec2  = Ref[WebGlTypeVec2.type]
  type RefVec3  = Ref[WebGlTypeVec3.type]
  type RefVec4  = Ref[WebGlTypeVec4.type]

  val RefInt   = (name: String) => Ref[WebGlTypeInt.type](name)
  val RefFloat = (name: String) => Ref[WebGlTypeFloat.type](name)
  val RefVec2  = (name: String) => Ref[WebGlTypeVec2.type](name)
  val RefVec3  = (name: String) => Ref[WebGlTypeVec3.type](name)
  val RefVec4  = (name: String) => Ref[WebGlTypeVec4.type](name)
}
