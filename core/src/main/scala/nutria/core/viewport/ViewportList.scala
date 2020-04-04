package nutria.core.viewport

import eu.timepit.refined.api.Validate
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{Empty, NonEmpty}
import nutria.core.ViewportList

case class Distinct()

object ViewportList {

  def apply(list: List[Viewport]): Either[String, ViewportList] = nutria.core.refine(list)

  def refineUnsafe(list: List[Viewport]): ViewportList = nutria.core.refineUnsafe(list)

  implicit val viewportListValidate: Validate[List[Viewport], NonEmpty And Distinct] =
    Validate.fromPredicate(
      f = list => list.nonEmpty && (list.distinct == list),
      showExpr = list => if (list.isEmpty) "List is empty" else "List is not distinct",
      p = And(Not(Empty()), Distinct())
    )
}
