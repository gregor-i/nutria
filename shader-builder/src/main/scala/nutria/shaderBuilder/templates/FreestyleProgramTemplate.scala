package nutria.shaderBuilder.templates

import nutria.core._
import nutria.shaderBuilder._
import mathParser.implicits._
import mathParser.Syntax._
import nutria.core.languages.Z

object FreestyleProgramTemplate extends Template[FreestyleProgram] {
  override def definitions(v: FreestyleProgram): Seq[String] =
    v.parameters.map {
      case IntParameter(name, value)   => constant[WebGlTypeInt.type](name, IntLiteral(value))
      case FloatParameter(name, value) => constant[WebGlTypeFloat.type](name, FloatLiteral(value))
      case RGBParameter(name, value)   => constant[WebGlTypeVec3.type](name, Vec3.fromRGB(value))
      case RGBAParameter(name, value)  => constant[WebGlTypeVec4.type](name, Vec4.fromRGBA(value))
      case fp: FunctionParameter if fp.includeDerivative =>
        Seq(
          function(fp.name, fp.value.node),
          function(fp.name + "_derived", fp.value.node.derive(Z))
        ).mkString("\n")
      case fp: FunctionParameter => function(fp.name, fp.value.node)
    }

  override def main(v: FreestyleProgram): String = v.code
}
