package nutria.frontend.pages

import facades.{Fs, SnabbdomToHtml}
import nutria.frontend._
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.scalajs.js
import scala.util.chaining._

class RenderUiSpec extends AnyFunSuite {
  Polyfil.init()

  for {
    (name, local, global) <- TestData.states
  } stateRenderingTest(s"Renders $name")(
    context = NoOpContext(local = local, global = global),
    fileName = s"./temp/${this.getClass.getSimpleName}/${name}.html"
  )

  def stateRenderingTest(testName: String)(context: Context[_ <: PageState], fileName: String) =
    test(testName) {
      Pages
        .ui(context)
        .toVNode
        .pipe(SnabbdomToHtml.apply)
        .pipe(withFixture)
        .pipe(write(fileName, _))
    }

  private def write(fileName: String, content: String): Unit = {
    Fs.mkdirSync(fileName.reverse.dropWhile(_ != '/').reverse, js.Dynamic.literal(recursive = true))
    Fs.writeFileSync(fileName, content)
  }

  private def withFixture(html: String): String =
    StaticContent("frontend/src/test/html/fixture.html")
      .replace("$content", html)
      .replace("src=\"/assets", "src=\"assets")
}
