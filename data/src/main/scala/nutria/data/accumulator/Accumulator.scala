package nutria.data.accumulator

trait Accumulator {
  type State
  def neutral: State
  def fold(left:State, right:Double) : State
  def lastOperation(result:State, count:Int) : Double

  def apply(collection:Traversable[Double]): Double =
      lastOperation(collection.foldLeft(neutral)(fold), collection.size)
}
