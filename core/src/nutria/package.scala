
package object nutria {
  type Fractal = (Double, Double) => Double

  type Accumulator = nutria.accumulator.Accumulator

  type Dimensions = nutria.viewport.Dimensions
  val Dimensions = nutria.viewport.Dimensions

  type Viewport = nutria.viewport.Viewport
  val Viewport = nutria.viewport.Viewport
  type Transform = nutria.viewport.Transform

  type Content = nutria.content.Content
  type FinishedContent = Content with nutria.content.Normalized

  type Color = nutria.color.Color

  type Image = nutria.image.Image
}
