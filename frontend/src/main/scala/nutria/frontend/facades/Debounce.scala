package nutria.frontend.facades

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("debounce", JSImport.Namespace)
object Debounce extends js.Any {
  def apply[A, B](arg1: js.Function1[A, B], timeout: Int = js.native): js.Function1[A, B] = js.native
}
