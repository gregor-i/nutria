package nutria.frontend

import nutria.api.User
import snabbdom.Node

import scala.reflect.ClassTag

abstract class Page[S <: PageState: ClassTag] extends ExecutionContext {
  type State  = S
  type Update = PageState => Unit

  def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState]

  def stateToUrl(state: State): Option[Router.Location]

  def render(implicit globalState: GlobalState, state: State, update: PageState => Unit): Node

  def acceptState(nutriaState: PageState): Boolean = implicitly[ClassTag[State]].runtimeClass == nutriaState.getClass
}
