package entities.color

object ConstantColor {
  val default: Color = new Gradient(
    new ConstantColor(0x00000000),
    new ConstantColor(0x000000ff),
    new ConstantColor(0x0000ffff),
    new ConstantColor(0x00ffffff)
  )

  val white = new ConstantColor(0x00ffffff)
  val black = new ConstantColor(0x00000000)

  val starter = Array(
    white,
    new ConstantColor(0x000000ff),
    new ConstantColor(0x0000ff00),
    new ConstantColor(0x0000ffff),
    new ConstantColor(0x00ff0000),
    new ConstantColor(0x00ff00ff),
    new ConstantColor(0x00ffff00),
    black)

  def interpolate(la: ConstantColor, lb: ConstantColor, p: Double): Int = {
    val a = la.farbe
    val a0 = 0xff & a
    val a1 = 0xff & (a >> 8)
    val a2 = 0xff & (a >> 16)
    val a3 = 0xff & (a >> 24)
    val b = lb.farbe
    val b0 = 0xff & b
    val b1 = 0xff & (b >> 8)
    val b2 = 0xff & (b >> 16)
    val b3 = 0xff & (b >> 24)
    val q = 1 - p
    (a3 * q + b3 * p).toInt << 24 | (a2 * q + b2 * p).toInt << 16 | (a1 * q + b1 * p).toInt << 8 | (a0 * q + b0 * p).toInt
  }
}

@SerialVersionUID(1L)
final case class ConstantColor(farbe: Int) extends Color {
  override def apply(in: Double): Int = farbe

  override def toString: String =
    "#%02x%02x%02x".format(0xff & (farbe >> 16), 0xff & (farbe >> 8), 0xff & farbe)
}
