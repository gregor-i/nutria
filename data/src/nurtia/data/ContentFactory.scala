package nurtia.data

import nutria.syntax._
import nutria._

trait ContentFactory extends ((Viewport, Dimensions, Fractal, Color) => Image)

case object SimpleFactory extends ContentFactory {
  def apply(view: Viewport, dim: Dimensions, fractal: Fractal, color:Color) =
    view.withDimensions(dim).withFractal(fractal).strongNormalized.withColor(color)
}

case object AntiAliaseFactory extends ContentFactory {
  def apply(view: Viewport, dim: Dimensions, fractal: Fractal, color:Color) =
    view.withDimensions(dim).withAntiAliasedFractal(fractal).strongNormalized.withColor(color)
}

case object BuddhaBrotFactory extends ContentFactory {
  def apply(view: Viewport, dim: Dimensions, fractal: Fractal, color:Color) =
    view.withDimensions(dim).withBuddhaBrot().strongNormalized.withColor(color)
}
