package nutria.data.colors

import nutria.core.colors.HSV

object WhiteToBlack extends HSV[Double]{
  def H(lambda: Double) = 0
  def S(lambda: Double) = 0
  def V(lambda: Double) = lambda
}
