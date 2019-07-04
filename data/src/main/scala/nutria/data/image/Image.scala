package nutria.data.image

import java.awt.image.BufferedImage
import java.io.File

object Image {
  def buffer(image: nutria.data.Image): BufferedImage = {
    val b = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for {
      w <- 0 until image.width
      h <- 0 until image.height
    } b.setRGB(w, h, image(w, h).hex)
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
