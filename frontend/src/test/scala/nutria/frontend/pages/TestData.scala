package nutria.frontend.pages

import nutria.api.{Entity, User, WithId}
import nutria.core.{Examples, FractalImage, IntParameter, Viewport}
import nutria.frontend.NutriaState

import scala.concurrent.Future

object TestData {
  private val fractalImage = FractalImage(template = Examples.timeEscape, viewport = Viewport.mandelbrot)
  private val fractalEntity = WithId(
    id = "id",
    owner = "owner",
    entity = Entity(
      value = fractalImage
    )
  )
  private val publicFractals = Vector.fill(10)(fractalEntity)
  private val owner          = Some(User("owner", "name", "email", None))
  private val user           = Some(User("user", "name", "email", None))

  val states: Seq[(String, NutriaState)] = Seq(
    "Loading"                      -> LoadingState(None, Future.failed(new Exception)),
    "Error"                        -> ErrorState(user, "error message"),
    "FAQ"                          -> FAQState(user = None),
    "Greeting"                     -> GreetingState(user, randomFractal = fractalImage),
    "Explorer-owner"               -> ExplorerState(user = owner, remoteFractal = Some(fractalEntity), fractalImage = fractalImage),
    "Explorer-no-user"             -> ExplorerState(user = None, remoteFractal = Some(fractalEntity), fractalImage = fractalImage),
    "Explorer-user"                -> ExplorerState(user = user, remoteFractal = Some(fractalEntity), fractalImage = fractalImage),
    "Gallery"                      -> GalleryState(user = None, publicFractals = publicFractals, votes = Map.empty),
    "Details-Diverging"            -> DetailsState(user = None, remoteFractal = fractalEntity, fractalToEdit = fractalEntity),
    "Template-Editor"              -> TemplateEditorState.initial(FAQState(user = None)),
    "Template-Editor-newParameter" -> TemplateEditorState.initial(FAQState(user = None)).copy(newParameter = Some(IntParameter("name", 5)))
  )
}
