package nutria.frontend.pages

import scala.scalajs.js
import scala.scalajs.js.Dynamic

object Polyfil extends js.Object {
  def init(): Unit = {
    Dynamic.global.eval("window = {}")

    Dynamic.global.window = new js.Object {
      def btoa(s: String): String = s
    }
  }
}
