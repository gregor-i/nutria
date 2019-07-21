package nutria.frontend.shaderBuilder

import java.util.UUID

import mathParser.algebra.SpireNode
import mathParser.{BinaryNode, ConstantNode, UnitaryNode, VariableNode}
import nutria.frontend.shaderBuilder.WebGlType.TypeProps
import spire.math.Complex

sealed trait WebGlStatement {
  def toCode: String
}

case class Declaration[T <: WebGlType : TypeProps](ref: Ref[T], expr: WebGlExpression[T]) extends WebGlStatement {
  def toCode: String = s"${TypeProps[T].webGlType} ${ref.name} = ${expr.toCode};"
}

case class Assignment[T <: WebGlType : TypeProps](ref: Ref[T], expr: WebGlExpression[T]) extends WebGlStatement {
  def toCode: String = s"${ref.name} = ${expr.toCode};"
}

object WebGlStatement {
  def assign[V](outputVar: Ref[WebGlTypeVec2.type],
                node: SpireNode[Complex[Double], V],
                varsToCode: PartialFunction[V, Ref[WebGlTypeVec2.type]]): Seq[WebGlStatement] = {
    var names = Map.empty[SpireNode[Complex[Double], V], Ref[WebGlTypeVec2.type]]
    var statements = List.empty[WebGlStatement]

    var nameCounter = 0
    def newName = {
      nameCounter += 1
      s"local_var_${nameCounter}"
    }

    def loop(node: SpireNode[Complex[Double], V]): Ref[WebGlTypeVec2.type] =
      node match {
        case c if names.contains(c) => names(c)
        case c@ConstantNode(Complex(real, imag)) =>
          val name = RefVec2(newName)
          statements = statements :+ Declaration(name, Vec2(FloatLiteral(real.toFloat), FloatLiteral(imag.toFloat)))
          names = names.updated(c, name)
          name
        case node@BinaryNode(op, childLeft, childRight) =>
          val refChildLeft = loop(childLeft)
          val refChildRight = loop(childRight)
          val name = RefVec2(newName)
          names = names.updated(node, name)
          statements = statements :+ Declaration(name, ComplexBinaryExp(op, RefExp(refChildLeft), RefExp(refChildRight)))
          name
        case UnitaryNode(op, child) =>
          val refChild = loop(child)
          val name = RefVec2(newName)
          statements = statements :+ Declaration(name, ComplexUnitaryExp(op, RefExp(refChild)))
          names = names.updated(node, name)
          name
        case VariableNode(v) => varsToCode(v)
      }

    val ref = loop(node)
    statements :+ Assignment(outputVar, RefExp(ref))
  }
}
