package nutria.frontend

import nutria.frontend.pages._
import snabbdom.Node

object Pages {
  val all: Seq[Page[_ <: NutriaState]] = Seq(
    ErrorPage,
    DetailsPage,
    ExplorerPage,
    GalleryPage,
    UserGalleryPage,
    LoadingPage,
    GreetingPage,
    FAQPage,
    ProfilePage,
    CreateNewFractalPage,
    AdminPage
  )

  def selectPage[State <: NutriaState](nutriaState: State): Page[State] =
    all
      .find(_.acceptState(nutriaState))
      .map(_.asInstanceOf[Page[State]])
      .getOrElse(throw new Exception(s"No Page defined for '${nutriaState.getClass.getSimpleName}'"))

  def ui(nutriaState: NutriaState, update: NutriaState => Unit): Node =
    selectPage(nutriaState).render(nutriaState, update)
}
