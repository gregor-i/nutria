package nutria.frontend

import monocle.macros.Lenses
import nutria.api.User
import nutria.frontend.util.Updatable

@Lenses
case class GlobalState(user: Option[User], navbarExpanded: Boolean = false)

object GlobalState {
  val initial: GlobalState = GlobalState(user = None)
}

trait PageState

trait Context[+S <: PageState] {
  def local: S
  def update(pageState: PageState): Unit

  def global: GlobalState
  def update(globalState: GlobalState): Unit

  def update(globalState: GlobalState, pageState: PageState): Unit

  // todo: try to get rid of it
  def localUpdatable: Updatable[S, PageState] = Updatable[S, PageState](local, update)
}

object Context {
  def apply(pageState: PageState, globalState: GlobalState, renderState: (GlobalState, PageState) => Unit): Context[PageState] =
    new Context[PageState] {
      def local: PageState                  = pageState
      def update(newState: PageState): Unit = renderState(globalState, newState)

      def global: GlobalState                 = globalState
      def update(newState: GlobalState): Unit = renderState(newState, pageState)

      def update(newGlobalState: GlobalState, newPageState: PageState): Unit = renderState(globalState, newPageState)
    }
}
