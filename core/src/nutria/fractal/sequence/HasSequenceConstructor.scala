package nutria.fractal.sequence

trait HasSequenceConstructor[A <: Sequence] {
  def sequence(x0: Double, y0: Double, maxIterations: Int): A
}
