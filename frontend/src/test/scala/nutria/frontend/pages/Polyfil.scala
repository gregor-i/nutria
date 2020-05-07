package nutria.frontend.pages

import scala.scalajs.js
import scala.scalajs.js.Dynamic

object Polyfil {
  def init(): Unit = {
    Dynamic.global.window = Dynamic.literal(btoa = (encoded => encoded): js.Function1[String, String])
  }
}
