package nutria.frontend

import nutria.frontend.util.Updatable
import snabbdom.Node

import scala.reflect.ClassTag

abstract class Page[S <: PageState: ClassTag] extends ExecutionContext {
  type State = S

  def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState]

  def stateToUrl(state: State): Option[Router.Location]

  def render(implicit globalState: GlobalState, updatable: Updatable[State, PageState]): Node

  def acceptState(nutriaState: PageState): Boolean = implicitly[ClassTag[State]].runtimeClass == nutriaState.getClass
}
