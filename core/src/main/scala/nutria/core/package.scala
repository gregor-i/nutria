package nutria

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.Positive
import nutria.core.viewport.Distinct

package object core {
  type Dimensions = viewport.Dimensions
  val Dimensions = viewport.Dimensions

  type Viewport = viewport.Viewport
  val Viewport = viewport.Viewport
  type Transform = viewport.Transform

  type Point = (Double, Double)

  type AntiAliase = Int Refined Positive

  type ViewportList = List[Viewport] Refined (NonEmpty And Distinct)

  def refine[A, V](a: A)(implicit validate: Validate[A, V]): Either[String, A Refined V] =
    eu.timepit.refined.refineV[V](a)

  def refineUnsafe[A, V](a: A)(implicit validate: Validate[A, V]): A Refined V = refine[A, V](a).toOption.get
}
