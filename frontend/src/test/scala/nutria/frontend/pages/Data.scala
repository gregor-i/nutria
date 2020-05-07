package nutria.frontend.pages

import nutria.core.{DivergingSeries, FractalEntity, FractalEntityWithId, FractalImage, NewtonIteration, ToFreestyle}
import nutria.frontend.NutriaState

import scala.concurrent.Future
import scala.util.chaining._

object Data {
  private val fractalImage = FractalImage(program = DivergingSeries.default.pipe(ToFreestyle.apply))
  private val fractalEntity = FractalEntityWithId(
    id = "id",
    owner = "owner",
    entity = FractalEntity(
      program = fractalImage.program
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
