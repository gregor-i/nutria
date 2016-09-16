package entities.accumulator

trait Accumulator {
  type State
  def neutral: State
  def fold(left:State, right:Double) : State
  def lastOperation(result:State, count:Int) : Double

  def apply(collection:TraversableOnce[Double]): Double =
      lastOperation(collection.foldLeft(neutral)(fold), collection.size)
}
