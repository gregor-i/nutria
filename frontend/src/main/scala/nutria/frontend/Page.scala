package nutria.frontend

import nutria.frontend.util.Updatable
import snabbdom.Node

import scala.reflect.ClassTag

abstract class Page[S <: PageState: ClassTag] extends ExecutionContext {
  type State  = S
  type Global = Page.Global
  type Local  = Page.Local[State]

  def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState]

  def stateToUrl(state: State): Option[Router.Location]

  def render(implicit global: Global, local: Local): Node

  def acceptState(nutriaState: PageState): Boolean = implicitly[ClassTag[State]].runtimeClass == nutriaState.getClass
}

object Page {
  type Global                = Updatable[GlobalState, GlobalState]
  type Local[S <: PageState] = Updatable[S, PageState]
}
