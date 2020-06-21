package nutria.frontend.pages

import facades.{Fs, HtmlFormatter, SnabbdomToHtml}
import nutria.frontend.{NutriaState, Pages}
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.chaining._

class GenerateHTMLSpec extends AnyFunSuite {
  Polyfil.init()

  test("generate the static html file") {
    LoadingState(user = None, loading = Future.never)
      .pipe(Pages.ui(_, _ => ()))
      .toVNode
      .pipe(SnabbdomToHtml.apply)
      .pipe(withFixture)
      .pipe(HtmlFormatter.render)
      .pipe(write("frontend/src/main/html/nutria.html", _))
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
    StaticContent("frontend/src/main/html/nutria-fixture.html")
      .replace("$content", html)
}
