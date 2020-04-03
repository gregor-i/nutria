package nutria.core.viewport

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{Empty, NonEmpty}
import eu.timepit.refined.refineV

case class Distinct()

object ViewportList {
  type ViewportList = List[Viewport] Refined (NonEmpty And Distinct)

  def apply(list: List[Viewport]): Either[String, ViewportList] = refineV(list)

  def ignoreError(list: List[Viewport]): ViewportList = apply(list).toOption.get

  implicit val viewportListValidate: Validate[List[Viewport], NonEmpty And Distinct] =
    Validate.fromPredicate(
      f = list => list.nonEmpty && (list.distinct == list),
      showExpr = list => if (list.isEmpty) "List is empty" else "List is not distinct",
      p = And(Not(Empty()), Distinct())
    )
}
