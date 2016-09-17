package entities.accumulator

case object Variance extends Accumulator {
  override type State = (Double, Double)
  override val neutral = (Arithmetic.neutral, Arithmetic.neutral)

  override def fold(left: (Double, Double), right: Double): (Double, Double) = {
    val fold1 = Arithmetic.fold(left._1, right)
    val fold2 = Arithmetic.fold(left._2, right * right)
    (fold1, fold2)
  }

  override def lastOperation(result: (Double, Double), count: Int): Double = {
    val result1 = Arithmetic.lastOperation(result._1, count)
    val result2 = Arithmetic.lastOperation(result._2, count)
    Math.sqrt(result2 - result1 * result1)
  }
}
