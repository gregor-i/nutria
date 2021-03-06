package nutria.frontend

import nutria.frontend.pages._
import snabbdom.Node

object Pages {
  val all: Seq[Page[_ <: PageState]] = Seq(
    ErrorPage,
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
    animated.NewtonFractalPage,
    animated.ColorfulFractalNoisePage,
    AdminPage
  )

  def selectPage[State <: PageState](pageState: State): Page[State] =
    all
      .find(_.acceptState(pageState))
      .map(_.asInstanceOf[Page[State]])
      .getOrElse(throw new Exception(s"No Page defined for '${pageState.getClass.getSimpleName}'"))

  def ui(context: Context[PageState]): Node =
    selectPage(context.local).render(context)
}
