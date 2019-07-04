package nutria.data

trait MathUtils {
  @inline final def q(@inline x: Double): Double = x * x
  @inline final def q3(@inline x: Double): Double = x * x * x
}
