package entities.accumulator

abstract class BinaryAccumulator(val _left: Accumulator, val _right: Accumulator) extends Accumulator {
  override type State = (_left.State, _right.State)

  override def neutral: State = (_left.neutral, _right.neutral)
  override def fold(input: State, next: Double): State =
    (_left.fold(input._1, next), _right.fold(input._2, next))
}

case class Norm(left: Accumulator, right: Accumulator) extends BinaryAccumulator(left, right) {
  override def lastOperation(result: State, count:Int): Double = {
    val l = _left.lastOperation(result._1, count)
    val r = _right.lastOperation(result._2, count)
    Math.sqrt(l*l + r*r)
  }
}

case class Add(left: Accumulator, right: Accumulator) extends BinaryAccumulator(left, right) {
  override def lastOperation(result: State, count:Int): Double = {
    val l = _left.lastOperation(result._1, count)
    val r = _right.lastOperation(result._2, count)
    l+r
  }
}

case class Sub(left: Accumulator, right: Accumulator) extends BinaryAccumulator(left, right) {
  override def lastOperation(result: State, count:Int): Double = {
    val l = _left.lastOperation(result._1, count)
    val r = _right.lastOperation(result._2, count)
    l-r
  }
}
