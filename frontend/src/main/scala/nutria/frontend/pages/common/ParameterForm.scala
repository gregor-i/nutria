package nutria.frontend.pages.common

import monocle.Lens
import monocle.function.Index
import nutria.core.{FloatParameter, FunctionParameter, InitialFunctionParameter, IntParameter, NewtonFunctionParameter, Parameter, RGBAParameter}
import nutria.frontend.util.{LenseUtils, SnabbdomUtil}
import snabbdom.SnabbdomFacade.Eventlistener
import snabbdom.{Node, SnabbdomFacade}

import scala.util.chaining._

object ParameterForm {
  def list[S](lens: Lens[S, Vector[Parameter]])(implicit state: S, update: S => Unit): Seq[Node] =
    lens
      .get(state)
      .indices
      .map { i =>
        val deleteAction = SnabbdomUtil.update(lens.modify(_.zipWithIndex.filter(_._2 != i).map(_._1)))
        val deleteButton = Button.icon(Icons.delete, deleteAction, round = false)

        lens
          .composeOptional(Index.index(i))
          .pipe(LenseUtils.unsafeOptional)
          .pipe(ParameterForm.apply(_, Seq(deleteButton)))
      }

  def apply[S](lens: Lens[S, Parameter], actions: Seq[Node] = Seq.empty)(implicit state: S, update: S => Unit): Node = {
    lens.get(state) match {
      case p: IntParameter =>
        val valueLens = lens.composePrism(Parameter.prismIntParameter).composeLens(IntParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, valueLens, actions)

      case p: FloatParameter =>
        val valueLens = lens.composePrism(Parameter.prismFloatParameter).composeLens(FloatParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, valueLens, actions)

      case p: RGBAParameter =>
        val valueLens = lens.composePrism(Parameter.prismRGBAParameter).composeLens(RGBAParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, valueLens, actions)

      case p: FunctionParameter =>
        val valueLens = lens.composePrism(Parameter.prismFunctionParameter).composeLens(FunctionParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, valueLens, actions)

      case p: InitialFunctionParameter =>
        val valueLens =
          lens.composePrism(Parameter.prismInitialFunctionParameter).composeLens(InitialFunctionParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, valueLens, actions)

      case p: NewtonFunctionParameter =>
        val valueLens =
          lens.composePrism(Parameter.prismNewtonFunctionParameter).composeLens(NewtonFunctionParameter.value).pipe(LenseUtils.unsafeOptional)
        Form.forLens(p.name, valueLens, actions)
    }
  }
}
