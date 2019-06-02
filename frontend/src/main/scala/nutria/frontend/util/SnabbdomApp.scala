package nutria.frontend.util

import com.raquo.snabbdom
import com.raquo.snabbdom.Snabbdom
import com.raquo.snabbdom.simple.{VNode, VNodeData}

trait SnabbdomApp {
  val patch = Snabbdom.init[VNode, VNodeData](snabbdom.builtInModules)
}

object SnabbdomApp
