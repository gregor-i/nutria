package nutria.staticRenderer

import org.scalajs.dom.raw.WebGLRenderingContext

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("gl", JSImport.Namespace)
object gl extends js.Object {
  def apply(width: Int, height: Int, options: js.Object): WebGLRenderingContext = js.native
}
