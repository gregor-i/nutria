package nutria.core

import mathParser.algebra.SpireNode
import nutria.core.languages.{StringFunction, ZAndLambda, ZAndZDerAndLambda}
import spire.math.Complex

object DivergingSeries {

  def deriveIteration(iteration: StringFunction[ZAndLambda]): SpireNode[Complex[Double], ZAndZDerAndLambda] = {
    import mathParser.algebra._
    import mathParser.{BinaryNode, ConstantNode, UnitaryNode, VariableNode}

    type C = Complex[Double]
    type V = nutria.core.languages.ZAndZDerAndLambda

    import mathParser.algebra.SpireLanguage.syntax._
    import nutria.core.languages._
    import spire.implicits._

    // todo: this is a copy of mathparser.derive with few changes ...
    def derive(term: SpireNode[C, V]): SpireNode[C, V] =
      term match {
        case VariableNode(Lambda)              => one[C, V]
        case VariableNode(Z)                   => VariableNode(ZDer)
        case VariableNode(ZDer)                => throw new IllegalArgumentException()
        case VariableNode(_) | ConstantNode(_) => zero[C, V]
        case UnitaryNode(op, f) =>
          op match {
            case Neg  => neg(derive(f))
            case Sin  => derive(f) * cos(f)
            case Cos  => neg(derive(f) * sin(f))
            case Tan  => derive(f) / (cos(f) * cos(f))
            case Asin => derive(f) / sqrt(one[C, V] - (f * f))
            case Acos => neg(derive(f)) / sqrt(one[C, V] - (f * f))
            case Atan => derive(f) / (one[C, V] + (f * f))
            case Sinh => derive(f) * cosh(f)
            case Cosh => derive(f) * sinh(f)
            case Tanh => derive(f) / (cosh(f) * cosh(f))
            case Exp  => derive(f) * exp(f)
            case Log  => derive(f) / f
          }
        case BinaryNode(op, f, g) =>
          op match {
            case Plus    => derive(f) + derive(g)
            case Minus   => derive(f) - derive(g)
            case Times   => (derive(f) * g) + (derive(g) * f)
            case Divided => ((derive(g) * f) - (derive(f) * g)) / (g * g)
            case Power   => (derive(f) * g * (f ^ (g - one[C, V]))) + (derive(g) * f * log(f))
          }
      }

    derive(iteration.node.asInstanceOf[SpireNode[C, V]])
  }
}
