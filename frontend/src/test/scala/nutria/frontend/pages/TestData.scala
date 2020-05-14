package nutria.frontend.pages

import nutria.api.WithId
import nutria.core.{Examples, FractalEntity, FractalImage, Viewport}
import nutria.frontend.NutriaState

import scala.concurrent.Future

object TestData {
  private val fractalImage = FractalImage(template = Examples.timeEscape, viewport = Viewport.mandelbrot)
  private val fractalEntity = WithId(
    id = "id",
    owner = "owner",
    entity = FractalEntity(
      program = fractalImage.template
    )
  )
  private val publicFractals = Vector.fill(10)(fractalEntity)

  val states: Seq[(String, NutriaState)] = Seq(
    "Loading"           -> LoadingState(Future.failed(new Exception)),
    "Error"             -> ErrorState("error message"),
    "FAQ"               -> FAQState(user = None),
    "Greeting"          -> GreetingState(randomFractal = fractalImage),
    "Explorer"          -> ExplorerState(user = None, remoteFractal = None, fractalImage = fractalImage),
    "Gallery"           -> GalleryState(user = None, publicFractals = publicFractals, votes = Map.empty),
    "Details_Diverging" -> DetailsState(user = None, remoteFractal = fractalEntity, fractalToEdit = fractalEntity)
  )
}
