package nutria.frontend.pages

import facades.{Fs, HtmlFormatter, SnabbdomToHtml}
import nutria.frontend._
import nutria.frontend.util.Updatable
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}
import scala.util.chaining._

class RenderUiSpec extends AnyFunSuite {
  Polyfil.init()

  for {
    (name, pageState, globalState) <- TestData.states
  } stateRenderingTest(s"Renders $name")(
    state = pageState,
    globalState = globalState,
    fileName = s"./temp/${this.getClass.getSimpleName}/${name}.html"
  )

  def stateRenderingTest(testName: String)(state: PageState, globalState: GlobalState, fileName: String): Unit =
    test(testName) {
      state
        .pipe(state => Pages.ui(globalState, Updatable(state, _ => ())))
        .toVNode
        .pipe(SnabbdomToHtml.apply)
        .pipe(withFixture)
        //        .pipe(HtmlFormatter.render)
        .pipe(write(fileName, _))
        .tap(_.onComplete {
          case Success(_)  => ()
          case Failure(ex) => println(ex)
        })
    }

  private def write(fileName: String, content: String): Future[Unit] = {
    for {
      _ <- Fs.promises
        .mkdir(fileName.reverse.dropWhile(_ != '/').reverse, js.Dynamic.literal(recursive = true))
        .toFuture
      _ <- Fs.promises.writeFile(fileName, content).toFuture
    } yield ()
  }

  private def withFixture(html: String): String =
    StaticContent("frontend/src/test/html/fixture.html")
      .replace("$content", html)
      .replace("src=\"/assets", "src=\"assets")
}
