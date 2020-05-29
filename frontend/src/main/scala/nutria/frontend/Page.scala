package nutria.frontend

import nutria.api.User
import nutria.frontend.Router.Location
import snabbdom.Node

import scala.reflect.ClassTag

abstract class Page[S <: NutriaState: ClassTag] extends ExecutionContext {
  type State  = S
  type Update = NutriaState => Unit

  def stateFromUrl: PartialFunction[(Option[User], Router.Path, Router.QueryParameter), NutriaState]

  def stateToUrl(state: State): Option[Router.Location]

  def render(implicit state: State, update: NutriaState => Unit): Node

  def acceptState(nutriaState: NutriaState): Boolean = implicitly[ClassTag[State]].runtimeClass == nutriaState.getClass
}

trait NoRouting[State <: NutriaState] {
  _: Page[State] =>

  override def stateFromUrl = PartialFunction.empty

  override def stateToUrl(state: State): Option[Location] = None
}
