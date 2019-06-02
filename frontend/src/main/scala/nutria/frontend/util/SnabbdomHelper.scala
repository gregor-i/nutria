package nutria.frontend.util

import com.raquo.snabbdom
import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.nodes.IterableNode
import com.raquo.snabbdom.simple.{VNode, VNodeData}

trait SnabbdomHelper {
  def vargs(s: VNode*) = new IterableNode(s.map(snabbdom.nodeToChildNode))
  def seq(s: Seq[VNode]) = new IterableNode(s.map(snabbdom.nodeToChildNode))

  def cond[A](b: Boolean, node: => A): Option[A] = if(b) Some(node) else None
}

object SnabbdomHelper extends SnabbdomHelper
