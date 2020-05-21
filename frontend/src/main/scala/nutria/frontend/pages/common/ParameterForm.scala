package nutria.frontend.pages.common

import monocle.Lens
import monocle.function.Index
import nutria.core.{FloatParameter, FunctionParameter, InitialFunctionParameter, IntParameter, NewtonFunctionParameter, Parameter, RGBAParameter}
import nutria.frontend.util.LenseUtils
import snabbdom.Node

import scala.util.chaining._

object ParameterForm {
  def list[S](lens: Lens[S, Vector[Parameter]])(implicit state: S, update: S => Unit): Seq[Node] =
    lens
      .get(state)
      .indices
      .map { i =>
        lens
          .composeOptional(Index.index(i))
          .pipe(LenseUtils.unsafeOptional)
          .pipe(ParameterForm.apply(_))
      }

  def apply[S](lens: Lens[S, Parameter])(implicit state: S, update: S => Unit): Node =
    lens.get(state) match {
      case p: IntParameter      => intParameter(p.name, lens.composePrism(Parameter.prismIntParameter).pipe(LenseUtils.unsafeOptional))
      case p: FloatParameter    => floatParameter(p.name, lens.composePrism(Parameter.prismFloatParameter).pipe(LenseUtils.unsafeOptional))
      case p: RGBAParameter     => rgbaParameter(p.name, lens.composePrism(Parameter.prismRGBAParameter).pipe(LenseUtils.unsafeOptional))
      case p: FunctionParameter => functionParameter(p.name, lens.composePrism(Parameter.prismFunctionParameter).pipe(LenseUtils.unsafeOptional))
      case p: InitialFunctionParameter =>
        initialFunctionParameter(p.name, lens.composePrism(Parameter.prismInitialFunctionParameter).pipe(LenseUtils.unsafeOptional))
      case p: NewtonFunctionParameter =>
        newtonFunctionParameter(p.name, lens.composePrism(Parameter.prismNewtonFunctionParameter).pipe(LenseUtils.unsafeOptional))
    }

  def intParameter[S](name: String, lens: Lens[S, IntParameter])(implicit state: S, update: S => Unit): Node =
    Form.intInput(name, lens.composeLens(IntParameter.value))
  def floatParameter[S](name: String, lens: Lens[S, FloatParameter])(implicit state: S, update: S => Unit): Node =
    Form.doubleInput(name, lens.composeLens(FloatParameter.value))
  def rgbaParameter[S](name: String, lens: Lens[S, RGBAParameter])(implicit state: S, update: S => Unit): Node =
    Form.colorInput(name, lens.composeLens(RGBAParameter.value))
  def functionParameter[S](name: String, lens: Lens[S, FunctionParameter])(implicit state: S, update: S => Unit): Node =
    Form.stringFunctionInput(name, lens.composeLens(FunctionParameter.value))
  def initialFunctionParameter[S](name: String, lens: Lens[S, InitialFunctionParameter])(implicit state: S, update: S => Unit): Node =
    Form.stringFunctionInput(name, lens.composeLens(InitialFunctionParameter.value))
  def newtonFunctionParameter[S](name: String, lens: Lens[S, NewtonFunctionParameter])(implicit state: S, update: S => Unit): Node =
    Form.stringFunctionInput(name, lens.composeLens(NewtonFunctionParameter.value))
}
