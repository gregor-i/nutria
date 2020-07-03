package nutria.frontend.pages.common

import monocle.Lens
import monocle.function.Index
import nutria.core.{
  ColorGradientParameter,
  FloatParameter,
  FunctionParameter,
  InitialFunctionParameter,
  IntParameter,
  NewtonFunctionParameter,
  Parameter,
  RGBAParameter
}
import nutria.frontend.util.{LenseUtils, SnabbdomUtil}
import snabbdom.SnabbdomFacade.Eventlistener
import snabbdom.{Node, SnabbdomFacade}

import scala.util.chaining._

object ParameterForm {
  def listWithActions[S](
      lens: Lens[S, Vector[Parameter]],
      actions: Parameter => Seq[(String, S => S)]
  )(implicit state: S, update: S => Unit): Seq[Node] =
    lens
      .get(state)
      .map { parameter =>
        lens
          .composeLens(
            Lens[Vector[Parameter], Parameter](get = _ => parameter)(
              set = newParameter =>
                oldParameters =>
                  oldParameters.map {
                    case `parameter`    => newParameter
                    case otherParameter => otherParameter
                  }
            )
          )
          .pipe(apply(_, actions(parameter)))
      }

  def list[S](lens: Lens[S, Vector[Parameter]])(implicit state: S, update: S => Unit): Seq[Node] =
    lens
      .get(state)
      .indices
      .map { i =>
        lens
          .composeOptional(Index.index(i))
          .pipe(LenseUtils.unsafeOptional)
          .pipe(apply(_))
      }

  def apply[S](lens: Lens[S, Parameter], actions: Seq[(String, S => S)] = Seq.empty)(implicit state: S, update: S => Unit): Node = {
    lens.get(state) match {
      case p: IntParameter =>
        val valueLens = lens.composePrism(Parameter.prismIntParameter).composeLens(IntParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, p.description, valueLens, actions)

      case p: FloatParameter =>
        val valueLens = lens.composePrism(Parameter.prismFloatParameter).composeLens(FloatParameter.value).pipe(LenseUtils.unsafeOptional)
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
