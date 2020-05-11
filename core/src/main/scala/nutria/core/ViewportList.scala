package nutria.core

import eu.timepit.refined.api.Validate
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{Empty, NonEmpty}
import nutria.core

case class Distinct()

object ViewportList {

  def apply(list: List[core.Viewport]): Either[String, ViewportList] = nutria.core.refine(list)

  def refineUnsafe(list: List[core.Viewport]): ViewportList = nutria.core.refineUnsafe(list)

  implicit val viewportListValidate: Validate[List[core.Viewport], NonEmpty And Distinct] =
    Validate.fromPredicate(
      f = list => list.nonEmpty && (list.distinct == list),
      showExpr = list => if (list.isEmpty) "List is empty" else "List is not distinct",
      p = And(Not(Empty()), Distinct())
    )
}
