package nutria.data.colors

case object Rainbow extends HSV[Double] {
  def H(lambda: Double) = lambda * 360 + 240
  def S(lambda: Double) = 1
  def V(lambda: Double) = 1
}
