package nutria.frontend.pages

import nutria.frontend.{Context, GlobalState, PageState}

case class NoOpContext[S <: PageState](local: S, global: GlobalState) extends Context[S] {
  override def update(pageState: PageState): Unit                           = ()
  override def update(globalState: GlobalState): Unit                       = ()
  override def update(globalState: GlobalState, pageState: PageState): Unit = ()
}
