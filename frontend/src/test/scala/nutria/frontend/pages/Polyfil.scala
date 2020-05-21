package nutria.frontend.pages

import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLCanvasElement

import scala.scalajs.js
import scala.scalajs.js.Dynamic

object Polyfil {
  def init(): Unit = {
    Dynamic.global.window = Dynamic.literal(
      btoa = (encoded => encoded): js.Function1[String, String],
      document = Dynamic.literal(
        createElement = (
            tag =>
              Dynamic.literal(
                getContext = (_ => Dynamic.literal()): js.Function1[String, js.Dynamic]
              )
          ): js.Function1[String, js.Dynamic]
      )
    )
  }
}
