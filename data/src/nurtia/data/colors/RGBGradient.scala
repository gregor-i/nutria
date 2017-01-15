package nurtia.data.colors

import nutria.core.Color
import nutria.core.colors.RGB

object RGBGradient {
  val default = RGBGradient(
    RGB.byHex(0x00000000),
    RGB.byHex(0x000000ff),
    RGB.byHex(0x0000ffff),
    RGB.byHex(0x00ffffff)
  )
}


case class RGBGradient(colors: RGB*) extends Color[Double] {
  private val n = colors.length - 1
  require(n > 0)

  override def apply(input: Double): RGB = {
    if (input <= 0) return colors(0)
    if (input >= 1) return colors(n)
    val rest = input * n
    val position = rest.toInt
    RGB.interpolate(colors(position), colors(position + 1), rest % 1)
  }
}
