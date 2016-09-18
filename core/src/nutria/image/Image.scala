package nutria
package image

import java.awt.image.BufferedImage

import nutria.color.Color
import nutria.viewport.HasDimensions

class Image(val content: FinishedContent, val farbe: Color) extends HasDimensions {
  val dimensions = content.dimensions

  val buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  for (w <- 0 until width; h <- 0 until height)
    buffer.setRGB(w, h, farbe(content(w, h)))
}
