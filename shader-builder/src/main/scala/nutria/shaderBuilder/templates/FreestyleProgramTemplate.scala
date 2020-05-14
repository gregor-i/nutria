package nutria.shaderBuilder.templates

import nutria.core.{DivergingSeries, _}
import nutria.shaderBuilder._
import mathParser.implicits._
import mathParser.Syntax._
import nutria.core.languages.{Lambda, StringFunction, X, Z}

object FreestyleProgramTemplate extends Template[FractalTemplate] {
  override def definitions(v: FractalTemplate): Seq[String] =
    v.parameters.map {
      case IntParameter(name, value)   => constant[WebGlTypeInt.type](name, IntLiteral(value))
      case FloatParameter(name, value) => constant[WebGlTypeFloat.type](name, FloatLiteral(value))
      case RGBParameter(name, value)   => constant[WebGlTypeVec3.type](name, Vec3.fromRGB(value))
      case RGBAParameter(name, value)  => constant[WebGlTypeVec4.type](name, Vec4.fromRGBA(value))
      case fp: FunctionParameter if fp.includeDerivative =>
        Seq(
          function(fp.name, fp.value.node),
          function(fp.name + "_derived", DivergingSeries.deriveIteration(fp.value))
        ).mkString("\n")
      case fp: FunctionParameter => function(fp.name, fp.value.node)

      case InitialFunctionParameter(name, value, includeDerivative) =>
        if (includeDerivative)
          function(name, value.node) + "\n" + function(name + "_derived", value.node.derive(Lambda))
        else
          function(name, value.node)

      case NewtonFunctionParameter(name, value, true) =>
        function(name, value.node) + "\n" + function(name + "_derived", value.node.derive(X))
    }

  override def main(v: FractalTemplate): String = v.code
}
