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

  def apply[S](lens: Lens[S, Parameter], actions: Seq[Node] = Seq.empty)(implicit state: S, update: S => Unit): Node =
    lens.get(state) match {
      case p: IntParameter   => intParameter(p.name, lens.composePrism(Parameter.prismIntParameter).pipe(LenseUtils.unsafeOptional), actions)
      case p: FloatParameter => floatParameter(p.name, lens.composePrism(Parameter.prismFloatParameter).pipe(LenseUtils.unsafeOptional), actions)
      case p: RGBAParameter  => rgbaParameter(p.name, lens.composePrism(Parameter.prismRGBAParameter).pipe(LenseUtils.unsafeOptional), actions)
      case p: FunctionParameter =>
        functionParameter(p.name, lens.composePrism(Parameter.prismFunctionParameter).pipe(LenseUtils.unsafeOptional), actions)
      case p: InitialFunctionParameter =>
        initialFunctionParameter(p.name, lens.composePrism(Parameter.prismInitialFunctionParameter).pipe(LenseUtils.unsafeOptional), actions)
      case p: NewtonFunctionParameter =>
        newtonFunctionParameter(p.name, lens.composePrism(Parameter.prismNewtonFunctionParameter).pipe(LenseUtils.unsafeOptional), actions)
    }

  def intParameter[S](name: String, lens: Lens[S, IntParameter], actions: Seq[Node])(
      implicit state: S,
      update: S => Unit
  ): Node =
    Form.intInput(name, lens.composeLens(IntParameter.value), actions)

  def floatParameter[S](name: String, lens: Lens[S, FloatParameter], actions: Seq[Node])(
      implicit state: S,
      update: S => Unit
  ): Node =
    Form.doubleInput(name, lens.composeLens(FloatParameter.value), actions)

  def rgbaParameter[S](name: String, lens: Lens[S, RGBAParameter], actions: Seq[Node])(
      implicit state: S,
      update: S => Unit
  ): Node =
    Form.colorInput(name, lens.composeLens(RGBAParameter.value), actions)

  def functionParameter[S](name: String, lens: Lens[S, FunctionParameter], actions: Seq[Node])(
      implicit state: S,
      update: S => Unit
  ): Node =
    Form.stringFunctionInput(name, lens.composeLens(FunctionParameter.value), actions)

  def initialFunctionParameter[S](name: String, lens: Lens[S, InitialFunctionParameter], actions: Seq[Node])(
      implicit state: S,
      update: S => Unit
  ): Node =
    Form.stringFunctionInput(name, lens = lens.composeLens(InitialFunctionParameter.value), actions)

  def newtonFunctionParameter[S](name: String, lens: Lens[S, NewtonFunctionParameter], actions: Seq[Node])(
      implicit state: S,
      update: S => Unit
  ): Node =
    Form.stringFunctionInput(name, lens = lens.composeLens(NewtonFunctionParameter.value), actions)
}
