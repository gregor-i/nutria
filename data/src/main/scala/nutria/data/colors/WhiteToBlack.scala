package nutria.data.colors

object WhiteToBlack extends HSV[Double]{
  def H(lambda: Double) = 0
  def S(lambda: Double) = 0
  def V(lambda: Double) = lambda
}
