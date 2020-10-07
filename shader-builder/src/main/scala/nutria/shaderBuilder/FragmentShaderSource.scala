package nutria.shaderBuilder

import mathParser.Implicits._
import mathParser.Syntax._
import mathParser.complex.{ComplexLanguage, ComplexNode}
import nutria.core.languages.{Lambda, Z}
import nutria.core.{DivergingSeries, _}
import nutria.macros.StaticContent

object FragmentShaderSource {
  def forTemplate(template: FractalTemplate, antiAliase: AntiAliase = 1): String =
    apply(template.code, template.parameters, antiAliase)

  def forImage(image: FractalImage, antiAliase: AntiAliase = 1): String =
    apply(image.template.code, image.appliedParameters, antiAliase)

  def apply(code: String, parameters: Vector[Parameter], antiAliase: AntiAliase): String = {
    s"""precision highp float;
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |
       |${StaticContent("shader-builder/src/main/glsl/global_definitions.glsl")}
       |
       |#line 1
       |${definitions(parameters)}
       |
       |#line 1
       |${code}
       |
       |void main() {
       |${AntiAliase(antiAliase, "gl_FragColor")}
       |}
    """.stripMargin
  }

  def function[V](name: String, node: ComplexNode[V])(implicit lang: ComplexLanguage[V]): String = {
    val optimized = PowerOptimizer.optimizer.optimize(node)
    nutria.shaderBuilder.Function(name, optimized)
  }

  def constant(name: String, expr: WebGlExpression[_]): String = {
    s"const ${expr.typeName} ${name} = ${expr.toCode};"
  }

  def parameter: Parameter => String = {
    case IntParameter(name, _, value)           => constant(name, IntLiteral(value))
    case FloatParameter(name, _, value)         => constant(name, FloatLiteral(value))
    case ComplexParameter(name, _, value)       => constant(name, Vec2(value.real, value.imag))
    case RGBAParameter(name, _, value)          => constant(name, Vec4.fromRGBA(value))
    case ColorGradientParameter(name, _, value) => colorGradient(name, value)
    case fp: FunctionParameter if fp.includeDerivative =>
      Seq(
        function(fp.name, fp.value.node),
        function(fp.name + "_derived", DivergingSeries.deriveIteration(fp.value))
      ).mkString("\n")
    case fp: FunctionParameter => function(fp.name, fp.value.node)

    case InitialFunctionParameter(name, _, value, includeDerivative) =>
      if (includeDerivative)
        function(name, value.node) + "\n" + function(name + "_derived", value.node.derive(Lambda))
      else
        function(name, value.node)

    case NewtonFunctionParameter(name, _, value, true) =>
      function(name, value.node) + "\n" + function(name + "_derived", value.node.derive(Z))
  }

  def definitions(parameters: Vector[Parameter]): String =
    parameters.map(parameter).mkString("\n")

  def colorGradient(name: String, colors: Seq[RGBA]): String = {

    val gradients =
      for ((Seq(colorLow, colorHigh), index) <- colors.sliding(2).zipWithIndex)
        yield s"if (i == ${index}) return mix(${Vec4.fromRGBA(colorLow).toCode}, ${Vec4.fromRGBA(colorHigh).toCode}, f);"

    val upperBound = s"return ${Vec4.fromRGBA(colors.last).toCode};"

    s"""
      |vec4 ${name} (in float low, in float high, in float value) {
      |  float tv = float(${colors.size - 1}) * (clamp(value, low, high) - low) / (high - low);
      |  int i = int(tv);
      |  float f = tv - floor(tv);
      |
      |  ${gradients.mkString("\n")}
      |  ${upperBound}
      |
      |}
      |""".stripMargin

  }
}
