package nutria.frontend.ui

import facades.{Fs, HtmlFormatter, SnabbdomToHtml}
import nutria.core.{DivergingSeries, FractalEntity, FractalEntityWithId, FractalImage, NewtonIteration}
import nutria.frontend.CreateNewFractalState.{FormulaStep}
import nutria.frontend._
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}
import scala.util.chaining._

class RenderUiSpec extends AnyFunSuite {
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
    "LoadingState"                    -> LoadingState(Future.failed(new Exception)),
    "ErrorState"                      -> ErrorState("error message"),
    "GreetingState"                   -> GreetingState(randomFractal = fractalImage),
    "ExplorerState"                   -> ExplorerState(user = None, remoteFractal = None, fractalImage = fractalImage),
    "GalleryState"                    -> GalleryState(user = None, publicFractals = publicFractals, votes = Map.empty),
    "DetailsState"                    -> DetailsState(user = None, remoteFractal = fractalEntity, fractalToEdit = fractalEntity),
    "CreateNewFractalState_init"      -> CreateNewFractalState(user = None),
    "CreateNewFractalState_newton"    -> CreateNewFractalState(user = None, step = FormulaStep(NewtonIteration.default)),
    "CreateNewFractalState_diverging" -> CreateNewFractalState(user = None, step = FormulaStep(DivergingSeries.default))
  )

  for {
    (name, state) <- states
  } stateRenderingTest(s"Renders $name")(
    state = state,
    fileName = s"./frontend/temp/${this.getClass.getSimpleName}/${name}.html"
  )

  def stateRenderingTest(testName: String)(state: NutriaState, fileName: String): Unit =
    test(testName) {
      state
        .pipe(Ui.apply(_, _ => ()))
        .toVNode
        .pipe(SnabbdomToHtml.apply)
        .pipe(withFixture)
        .pipe(HtmlFormatter.render)
        .pipe(write(fileName, _))
        .tap(_.onComplete {
          case Success(_)  => ()
          case Failure(ex) => println(ex)
        })
//        .pipe(_.isReadyWithin(Span(2, Seconds)))
//        .tap(assert(_))
    }

  private def write(fileName: String, content: String): Future[Unit] = {
    for {
      _ <- Fs.promises.mkdir(fileName.reverse.dropWhile(_ != '/').reverse, js.Dynamic.literal(recursive = true)).toFuture
      _ <- Fs.promises.writeFile(fileName, content).toFuture
    } yield ()
  }

  private def withFixture(html: String): String =
    StaticContent("frontend/src/test/html/fixture.html")
      .replaceAllLiterally("$content", html)
      .replaceAllLiterally("src=\"/img", "src=\"img")
}
