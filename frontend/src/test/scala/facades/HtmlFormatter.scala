package facades

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("html-formatter", name = JSImport.Default)
object HtmlFormatter extends js.Object {
  def render(html: String): String = js.native
}
