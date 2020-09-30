package nutria.frontend

import nutria.frontend.pages._
import snabbdom.Node

object Pages {
  val all: Seq[Page[_ <: PageState]] = Seq(
    ErrorPage,
    DetailsPage,
    ExplorerPage,
    GalleryPage,
    UserGalleryPage,
    LoadingPage,
    GreetingPage,
    DocumentationPage,
    ProfilePage,
    TemplateEditorPage,
    TemplateGalleryPage,
    CreateNewFractalPage,
    NewtonFractalDesignePage,
    AdminPage
  )

  def selectPage[State <: PageState](nutriaState: State): Page[State] =
    all
      .find(_.acceptState(nutriaState))
      .map(_.asInstanceOf[Page[State]])
      .getOrElse(throw new Exception(s"No Page defined for '${nutriaState.getClass.getSimpleName}'"))

  def ui(globalState: GlobalState, pageState: PageState, update: PageState => Unit): Node =
    selectPage(pageState).render(globalState, pageState, update)
}
