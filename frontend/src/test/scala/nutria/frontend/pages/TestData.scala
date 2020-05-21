package nutria.frontend.pages

import nutria.api.{Entity, WithId}
import nutria.core.{Examples, Fractal, FractalImage, IntParameter, Viewport}
import nutria.frontend.NutriaState

import scala.concurrent.Future

object TestData {
  private val fractalImage = FractalImage(template = Examples.timeEscape, viewport = Viewport.mandelbrot)
  private val fractalEntity = WithId(
    id = "id",
    owner = "owner",
    entity = Entity(
      value = Fractal(
        program = fractalImage.template
      )
    )
  )
  private val publicFractals = Vector.fill(10)(fractalEntity)

  val states: Seq[(String, NutriaState)] = Seq(
    "Loading"                      -> LoadingState(Future.failed(new Exception)),
    "Error"                        -> ErrorState("error message"),
    "FAQ"                          -> FAQState(user = None),
    "Greeting"                     -> GreetingState(randomFractal = fractalImage),
    "Explorer"                     -> ExplorerState(user = None, remoteFractal = None, fractalImage = fractalImage),
    "Gallery"                      -> GalleryState(user = None, publicFractals = publicFractals, votes = Map.empty),
    "Details-Diverging"            -> DetailsState(user = None, remoteFractal = fractalEntity, fractalToEdit = fractalEntity),
    "Template-Editor"              -> TemplateEditorState.initial(FAQState(user = None)),
    "Template-Editor-newParameter" -> TemplateEditorState.initial(FAQState(user = None)).copy(newParameter = Some(IntParameter("name", 5)))
  )
}
