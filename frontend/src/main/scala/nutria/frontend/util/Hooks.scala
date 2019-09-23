package nutria.frontend.util

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.hooks.NodeHooks
import com.raquo.snabbdom.simple.{VNode => N, VNodeData => D}

import scala.scalajs.js.UndefOr

object Hooks {
  def apply(f: NodeHooks[N, D] => Unit): Modifier[N, D] =
    node => f(hooks(node))

  def insertHook(f: NodeHooks[N, D]#NewNode => Any): Modifier[N, D] =
    apply(_.addInsertHook(f))

  def postPatchHook(f: (NodeHooks[N, D]#OldNode, NodeHooks[N, D]#NewNode) => Any): Modifier[N, D] =
    apply(_.addPostPatchHook(f))

  def destroyHook(f: NodeHooks[N, D]#OldNode => Any): Modifier[N, D] =
    apply(_.addDestroyHook(f))

  private def hooks(node: N): NodeHooks[N, D] = {
    if (node.data.hooks.isEmpty) {
      node.data.hooks = UndefOr.any2undefOrA(new NodeHooks())
    }
    node.data.hooks.get
  }
}
