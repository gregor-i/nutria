package nutria.shaderBuilder

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.languages.{CLang, CNode, Lambda, X}
import nutria.core.{DivergingSeries, _}
import nutria.macros.StaticContent
import nutria.shaderBuilder.WebGlType.TypeProps

object FragmentShaderSource {
  def apply(state: FractalTemplate, antiAliase: AntiAliase): String = {
    s"""${definitions(state)}
       |${FragmentShaderSource.main(state)}
       |
       |void main() {
       |${AntiAliase(antiAliase).apply(RefVec4("gl_FragColor"))}
       |}
    """.stripMargin
  }

  def function[V](name: String, node: CNode[V])(implicit lang: CLang[V]): String =
    nutria.shaderBuilder.Function(name, node)

  def constant[T <: WebGlType: TypeProps](name: String, expr: WebGlExpression[T]): String = {
    s"const ${TypeProps[T].webGlType} ${name} = ${expr.toCode};"
  }

  def parameter: Parameter => String = {
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

  def definitions(template: FractalTemplate): String =
    s"""precision highp float;
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |
       |${StaticContent("shader-builder/src/main/glsl/global_definitions.glsl")}
       |
       |${template.parameters.map(parameter).mkString("\n")}""".stripMargin

  def main(v: FractalTemplate): String =
    s"""vec4 main_template(const in vec2 p) {
       |${v.code.linesIterator.map("  " + _).mkString("\n")}
       |  return vec4(0.0);
       |}""".stripMargin
}
