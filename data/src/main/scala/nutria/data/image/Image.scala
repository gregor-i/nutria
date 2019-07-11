package nutria.data.image

import java.awt.image.{BufferedImage, DataBufferInt}
import java.io.File

object Image {
  def buffer(image: nutria.data.Image): BufferedImage = {
    val b = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
    val data = b.getRaster.getDataBuffer.asInstanceOf[DataBufferInt]
    for {
      w <- 0 until image.width
      h <- 0 until image.height
    } {
      val color = image(w, h)
      val hex: Int = (color.A * 255).toInt << 24 | color.R.toInt << 16 | color.G.toInt << 8 | color.B.toInt
      data.setElem(w + h * image.width, hex)
    }
    b
  }

  def save(image: nutria.data.Image, file: java.io.File): File = {
    if (file.getParentFile != null)
      file.getParentFile.mkdirs()
    javax.imageio.ImageIO.write(buffer(image), "png", file)
    file
  }

  def verboseSave(image: nutria.data.Image, file: java.io.File): File = {
    save(image, file)
    println("Saved: " + file.getAbsoluteFile.toString)
    file
  }
}
