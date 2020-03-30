package nutria.frontend.ui

import facades.{Fs, HtmlFormatter, SnabbdomToHtml}
import nutria.core.{DivergingSeries, FractalEntity, FractalEntityWithId, FractalImage}
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
  private val publicFractals = Vector.tabulate(10) { i =>
    FractalEntityWithId(
      id = i.toString,
      owner = i.toString,
      entity = FractalEntity(
        program = fractalImage.program
      )
    )
  }

  val states: Seq[NutriaState] = Seq(
    LoadingState(Future.failed(new Exception)),
    ErrorState("error message"),
    GreetingState(randomFractal = fractalImage),
    ExplorerState(user = None, remoteFractal = None, fractalImage = fractalImage),
    GalleryState(user = None, publicFractals = publicFractals, votes = Map.empty),
    CreateNewFractalState(user = None)
  )

  for {
    state <- states
    name = state.getClass.getSimpleName
  } stateRenderingTest(s"$name: (${state.hashCode()})")(
    state = state,
    fileName = s"./frontend/temp/${this.getClass.getSimpleName}/${name}_${state.hashCode()}.html"
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
      .replaceAllLiterally("src=\"/img", "src=\"../../../backend/public/img")
}
