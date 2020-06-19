package nutria.core

import mathParser.complex._
import nutria.core.languages._

object DivergingSeries {

  def deriveIteration(iteration: StringFunction[ZAndLambda]): ComplexNode[ZAndZDerAndLambda] = {
    import mathParser.{BinaryNode, ConstantNode, UnitaryNode, VariableNode}

    type V = nutria.core.languages.ZAndZDerAndLambda

    import mathParser.complex.ComplexLanguage.syntax._

    // todo: this is a copy of mathparser.derive with few changes ...
    def derive(term: ComplexNode[V]): ComplexNode[V] =
      term match {
        case VariableNode(Lambda)              => one[V]
        case VariableNode(Z)                   => VariableNode(ZDer)
        case VariableNode(ZDer)                => throw new IllegalArgumentException()
        case VariableNode(_) | ConstantNode(_) => zero[V]
        case UnitaryNode(op, f) =>
          op match {
            case Neg  => neg(derive(f))
            case Sin  => derive(f) * cos(f)
            case Cos  => neg(derive(f) * sin(f))
            case Tan  => derive(f) / (cos(f) * cos(f))
            case Asin => derive(f) / sqrt(one[V] - (f * f))
            case Acos => neg(derive(f)) / sqrt(one[V] - (f * f))
            case Atan => derive(f) / (one[V] + (f * f))
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
            case Power   => (derive(f) * g * (f ^ (g - one[V]))) + (derive(g) * f * log(f))
          }
      }

    derive(iteration.node.asInstanceOf[ComplexNode[V]])
  }
}
