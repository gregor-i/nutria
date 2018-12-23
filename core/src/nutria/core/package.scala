package nutria

package object core {
  type Dimensions = nutria.core.viewport.Dimensions
  val  Dimensions = nutria.core.viewport.Dimensions

  type Viewport = nutria.core.viewport.Viewport
  val  Viewport = nutria.core.viewport.Viewport
  type Transform = nutria.core.viewport.Transform

  type Point = (Double, Double)

  type Content[A] = nutria.core.content.Content[A]

  type RGB = nutria.core.colors.RGB
  type Color[A] = A => RGB

  type Image = Content[RGB]
}
