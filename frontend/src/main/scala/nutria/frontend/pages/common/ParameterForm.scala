package nutria.frontend.pages.common

import monocle.Lens
import nutria.core._
import nutria.frontend.util.{LenseUtils, Updatable}
import snabbdom.Node

import scala.util.chaining._

object ParameterForm {
  def list[S](
      lens: Lens[S, Vector[Parameter]],
      actions: Parameter => Seq[(String, S => S)] = (_: Parameter) => Seq.empty
  )(implicit updatable: Updatable[S, S]): Seq[Node] =
    lens
      .get(updatable.state)
      .sorted
      .map { parameter =>
        lens
          .composeLens(LenseUtils.seqWhere(parameter))
          .pipe(apply(_, actions(parameter)))
      }

  def apply[S](
      lens: Lens[S, Parameter],
      actions: Seq[(String, S => S)] = Seq.empty
  )(implicit updatable: Updatable[S, S]): Node = {
    lens.get(updatable.state) match {
      case p: IntParameter =>
        val valueLens = lens.composePrism(Parameter.prismIntParameter).composeLens(IntParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: FloatParameter =>
        val valueLens = lens.composePrism(Parameter.prismFloatParameter).composeLens(FloatParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: ComplexParameter =>
        val valueLens = lens.composePrism(Parameter.prismComplexParameter).composeLens(ComplexParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: RGBAParameter =>
        val valueLens = lens.composePrism(Parameter.prismRGBAParameter).composeLens(RGBAParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: ColorGradientParameter =>
        val valueLens =
          lens.composePrism(Parameter.prismColorGradientParameter).composeLens(ColorGradientParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: FunctionParameter =>
        val valueLens = lens.composePrism(Parameter.prismFunctionParameter).composeLens(FunctionParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: InitialFunctionParameter =>
        val valueLens =
          lens.composePrism(Parameter.prismInitialFunctionParameter).composeLens(InitialFunctionParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: NewtonFunctionParameter =>
        val valueLens =
          lens.composePrism(Parameter.prismNewtonFunctionParameter).composeLens(NewtonFunctionParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)
    }
  }
}
