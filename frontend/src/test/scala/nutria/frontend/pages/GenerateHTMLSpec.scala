package nutria.frontend.pages

import facades.{Fs, HtmlFormatter, SnabbdomToHtml}
import nutria.frontend.{GlobalState, Pages}
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.Future
import scala.scalajs.js
import scala.util.chaining._

class GenerateHTMLSpec extends AnyFunSuite {
  Polyfil.init()

  test("generate the static html file") {
    Pages
      .ui(NoOpContext(local = LoadingState(process = Future.never), global = GlobalState.initial))
      .toVNode
      .pipe(SnabbdomToHtml.`default`)
      .pipe(withFixture)
      .pipe(HtmlFormatter.render)
      .pipe(write("frontend/src/main/html/nutria.html", _))
  }

  private def write(fileName: String, content: String): Unit = {
    Fs.mkdirSync(fileName.reverse.dropWhile(_ != '/').reverse, js.Dynamic.literal(recursive = true))
    Fs.writeFileSync(fileName, content)
  }

  private def withFixture(html: String): String =
    StaticContent("frontend/src/main/html/nutria-fixture.html")
      .replace("$content", html)
}
