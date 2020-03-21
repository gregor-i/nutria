package nutria.shaderBuilder

import mathParser.{Derive, Language, Node, Optimizer}

object Syntax {

  // todo: move to mathparser
  implicit class EnrichNode[UO, BO, S, V](node: Node[UO, BO, S, V])(
      implicit lang: Language[UO, BO, S, V]
  ) {
    def optimize(implicit optimizer: Optimizer[UO, BO, S, V]): Node[UO, BO, S, V] =
      lang.optimize(node)(optimizer)

    def derive(variable: V)(implicit derive: Derive[UO, BO, S, V]): Node[UO, BO, S, V] =
      lang.derive(node)(variable)
  }

}
