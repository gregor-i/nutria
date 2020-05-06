package nutria.frontend.pages

import facades.{Fs, HtmlFormatter, SnabbdomToHtml}
import nutria.frontend._
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
    (name, state) <- Data.states
  } stateRenderingTest(s"Renders $name")(
    state = state,
    fileName = s"./temp/${this.getClass.getSimpleName}/${name}.html"
  )

  def stateRenderingTest(testName: String)(state: NutriaState, fileName: String): Unit =
    test(testName) {
      state
        .pipe(Pages.ui(_, _ => ()))
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
      _ <- Fs.promises
        .mkdir(fileName.reverse.dropWhile(_ != '/').reverse, js.Dynamic.literal(recursive = true))
        .toFuture
      _ <- Fs.promises.writeFile(fileName, content).toFuture
    } yield ()
  }

  private def withFixture(html: String): String =
    StaticContent("frontend/src/test/html/fixture.html")
      .replaceAllLiterally("$content", html)
      .replaceAllLiterally("src=\"/img", "src=\"img")
}
