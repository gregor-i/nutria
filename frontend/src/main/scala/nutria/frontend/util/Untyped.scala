package nutria.frontend.util

import scala.scalajs.js

object Untyped {
  @inline
  def apply(any:js.Object): js.Dynamic = any.asInstanceOf[js.Dynamic]
}
