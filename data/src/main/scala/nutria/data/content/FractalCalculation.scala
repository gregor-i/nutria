package nutria.data.content

import nutria.core.Point
import nutria.data.RGB

trait FractalCalculation {
  type A
  def content(p:Point): A
  def postprocessing(raw: Content[A]): Content[RGB]
}

object FractalCalculation {
  def apply(_fractal: Point => RGB): FractalCalculation = new FractalCalculation {
    override type A = RGB
    override def content(p: Point) = _fractal(p)
    override def postprocessing(raw: Content[A]): Content[RGB] = raw
  }

  def apply[_A](_fractal: Point => _A, _coloring: Content[_A] => Content[RGB]): FractalCalculation =
    new FractalCalculation {
      override type A = _A
      override def content(p: Point) = _fractal(p)
      override def postprocessing(raw: Content[A]): Content[RGB] = _coloring(raw)
    }
}
