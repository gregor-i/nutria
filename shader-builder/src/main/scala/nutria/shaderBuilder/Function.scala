package nutria.shaderBuilder

import mathParser.complex._
import mathParser.{BinaryNode, ConstantNode, UnitaryNode}

object Function {
  def apply[V](name: String, node: ComplexNode[V])(implicit lang: ComplexLanguage[V]): String = {
    val statements = flattenIntoStatements(node, lang)

    s"""vec2 $name(${lang.variables.map(t => s"const in vec2 ${t._1}").mkString(", ")}) {
       |${statements.map("  " + _).mkString("\n")}
       |}
       |""".stripMargin
  }

  private def flattenIntoStatements[V](node: ComplexNode[V], lang: ComplexLanguage[V]): List[String] = {
    var names = lang.variables
      .map(v => (lang.variable(v._2), v._1))
      .toMap[ComplexNode[V], String]
    var statements = List.empty[String]

    var nameCounter = 0
    def newName() = {
      nameCounter += 1
      s"var_${nameCounter}"
    }

    def loop(node: ComplexNode[V]): String =
      node match {
        case c if names.contains(c) => names(c)
        case ConstantNode(Complex(real, imag)) =>
          Vec2(FloatLiteral(real.toFloat), FloatLiteral(imag.toFloat)).toCode
        case node @ BinaryNode(op, childLeft, childRight) =>
          val refChildLeft  = loop(childLeft)
          val refChildRight = loop(childRight)
          val name          = newName()
          names = names.updated(node, name)
          statements = statements :+ s"vec2 $name = ${binaryNodeToCode(op, refChildLeft, refChildRight)};"
          name
        case UnitaryNode(op, child) =>
          val refChild = loop(child)
          val name     = newName()
          statements = statements :+ s"vec2 $name = ${unitaryNodeToCode(op, refChild)};"
          names = names.updated(node, name)
          name
      }

    val ref = loop(node)
    statements :+ s"return ${ref};"
  }

  private def binaryNodeToCode(
      op: ComplexBinaryOperator,
      left: String,
      right: String
  ): String =
    op match {
      case Plus    => s"$left + $right"
      case Minus   => s"$left - $right"
      case Times   => s"complex_product($left, $right)"
      case Divided => s"complex_divide($left, $right)"
      case Power   => s"complex_power($left, $right)"
    }

  private def unitaryNodeToCode(
      op: ComplexUnitaryOperator,
      child: String
  ): String =
    op match {
      case Neg  => s"-($child)"
      case Sin  => s"complex_sin($child)"
      case Cos  => s"complex_cos($child)"
      case Tan  => s"complex_tan($child)"
      case Asin => s"complex_asin($child)"
      case Acos => s"complex_acos($child)"
      case Atan => s"complex_atan($child)"
      case Sinh => s"complex_sinh($child)"
      case Cosh => s"complex_cosh($child)"
      case Tanh => s"complex_tanh($child)"
      case Exp  => s"complex_exp($child)"
      case Log  => s"complex_log($child)"
    }
}
