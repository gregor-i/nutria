package nutria.data

trait DoubleSequence extends Iterator[(Double, Double)] { self =>
  def state: (Double, Double)
}
