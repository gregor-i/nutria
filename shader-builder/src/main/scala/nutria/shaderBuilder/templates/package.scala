package nutria.shaderBuilder

import nutria.core.languages.{CLang, CNode}
import nutria.shaderBuilder.WebGlType.TypeProps

package object templates {
  def function[V](name: String, node: CNode[V])(implicit lang: CLang[V]): String = {
    val result = RefVec2("result")
    val varsToName = lang.variables
      .map[PartialFunction[V, RefVec2]] { case (name, v) => { case `v` => RefVec2(name) } }
      .fold(PartialFunction.empty)(_ orElse _)
    s"""vec2 $name(${lang.variables.map(t => s"vec2 ${t._1}").mkString(", ")}) {
       |  ${WebGlStatement.blockDeclare(result, node, varsToName)}
       |  return ${result.name};
       |}
       |""".stripMargin
  }

  def constant[T <: WebGlType: TypeProps](name: String, expr: WebGlExpression[T]): String = {
    s"const ${TypeProps[T].webGlType} ${name} = ${expr.toCode};"
  }
}
