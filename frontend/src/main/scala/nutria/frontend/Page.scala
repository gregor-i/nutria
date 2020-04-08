package nutria.frontend

import nutria.frontend.Router.Location
import snabbdom.Node

import scala.reflect.ClassTag

abstract class Page[S <: NutriaState: ClassTag] {
  type State  = S
  type Update = NutriaState => Unit

  def stateFromUrl: PartialFunction[Router.Location, NutriaState]

  def stateToUrl(state: State): Option[Router.Location]

  def render(implicit state: State, update: NutriaState => Unit): Node

  def acceptState(nutriaState: NutriaState): Boolean = implicitly[ClassTag[State]].runtimeClass == nutriaState.getClass
}

trait NoRouting[State <: NutriaState] {
  _: Page[State] =>

  override def stateFromUrl: PartialFunction[Location, NutriaState] = PartialFunction.empty

  override def stateToUrl(state: State): Option[Location] = None
}
