package nutria.frontend

import nutria.frontend.pages._
import nutria.frontend.util.Updatable
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

  def ui(globalState: GlobalState, updatable: Updatable[PageState, PageState]): Node =
    selectPage(updatable.state).render(globalState, updatable)
}
