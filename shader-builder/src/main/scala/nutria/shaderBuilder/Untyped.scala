package nutria.shaderBuilder

import scala.scalajs.js

object Untyped {
  @inline
  private[shaderBuilder] def apply(any: js.Object): js.Dynamic = any.asInstanceOf[js.Dynamic]
}
