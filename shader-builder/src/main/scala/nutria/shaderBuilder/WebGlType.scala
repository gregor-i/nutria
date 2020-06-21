package nutria.shaderBuilder

sealed trait WebGlType
case object WebGlTypeInt   extends WebGlType
case object WebGlTypeFloat extends WebGlType
case object WebGlTypeVec3  extends WebGlType
case object WebGlTypeVec2  extends WebGlType
case object WebGlTypeVec4  extends WebGlType
