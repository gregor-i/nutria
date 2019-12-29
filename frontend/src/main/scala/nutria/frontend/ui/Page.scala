package nutria.frontend.ui

import nutria.frontend.NutriaState
import snabbdom.Node

trait Page[State <: NutriaState] {
  def render(implicit state: State, update: NutriaState => Unit): Seq[Node]
}
