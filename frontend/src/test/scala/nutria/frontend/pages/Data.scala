package nutria.frontend.pages

import nutria.core.{DivergingSeries, FractalEntity, FractalEntityWithId, FractalImage, NewtonIteration}
import nutria.frontend.pages.CreateNewFractalState.FormulaStep
import nutria.frontend.NutriaState

import scala.concurrent.Future

object Data {
  private val fractalImage = FractalImage(program = DivergingSeries.default)
  private val fractalEntity = FractalEntityWithId(
    id = "id",
    owner = "owner",
    entity = FractalEntity(
      program = fractalImage.program
    )
  )
  private val publicFractals = Vector.fill(10)(fractalEntity)

  val states: Seq[(String, NutriaState)] = Seq(
    "Loading"                    -> LoadingState(Future.failed(new Exception)),
    "Error"                      -> ErrorState("error message"),
    "FAQ"                        -> FAQState(user = None),
    "Greeting"                   -> GreetingState(randomFractal = fractalImage),
    "Explorer"                   -> ExplorerState(user = None, remoteFractal = None, fractalImage = fractalImage),
    "Gallery"                    -> GalleryState(user = None, publicFractals = publicFractals, votes = Map.empty),
    "Details_Diverging"          -> DetailsState(user = None, remoteFractal = fractalEntity, fractalToEdit = fractalEntity),
    "CreateNewFractal_init"      -> CreateNewFractalState(user = None),
    "CreateNewFractal_newton"    -> CreateNewFractalState(user = None, step = FormulaStep(NewtonIteration.default)),
    "CreateNewFractal_diverging" -> CreateNewFractalState(user = None, step = FormulaStep(DivergingSeries.default))
  )
}
