package entities.accumulator

case object Arithmetic extends Accumulator {
  override type State = Double
  override val neutral: Double  = 0d
  override def fold(left: Double, right: Double): Double = left + right
  override def lastOperation(result: Double, count: Int): Double = result / count
}

case object Geometric extends Accumulator {
  override type State = Double
  override val neutral: Double = 1d
  override def fold(left: Double, right: Double): Double = left * right
  override def lastOperation(result: Double, count: Int): Double = Math.pow(result, 1d/count)
}

case object  Harmonic extends Accumulator {
  override type State = Double
  override val neutral: Double = 0d
  override def fold(left: Double, right: Double): Double = left + 1d/right
  override def lastOperation(result: Double, count: Int): Double = count / result
}

case object Max extends Accumulator {
  override type State = Double
  override val neutral: Double = java.lang.Double.NEGATIVE_INFINITY
  override def fold(left: Double, right: Double): Double = Math.max(left, right)
  override def lastOperation(result: Double, count: Int): Double = result
}

case object Min extends Accumulator {
  override type State = Double
  override val neutral: Double = java.lang.Double.POSITIVE_INFINITY
  override def fold(left: Double, right: Double): Double = Math.min(left, right)
  override def lastOperation(result: Double, count: Int): Double = result
}
