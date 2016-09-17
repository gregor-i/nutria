package entities.color

import java.util.ArrayList
import java.util.Arrays

case class Gradient(farben: ConstantColor*) extends Color {
  private val n = farben.length - 1
  require(n > 0)

  override def apply(input: Double): Int = {
    if (input <= 0) return farben(0).farbe
    if (input >= 1) return farben(n).farbe
    val rest = input * n
    val stelle = rest.toInt
    ConstantColor.interpolate(farben(stelle), farben(stelle + 1), rest % 1)
  }

  override def toString(): String = {
    String.format("GradientN %s", farben.toString())
  }
}
