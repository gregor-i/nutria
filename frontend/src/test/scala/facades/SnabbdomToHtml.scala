package facades

import snabbdom.VNode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("snabbdom-to-html", JSImport.Namespace)
object SnabbdomToHtml extends js.Object {
  def `default`(vNode: VNode): String = js.native
}
