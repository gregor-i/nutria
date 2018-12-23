package nutria.core.image

import java.awt.image.BufferedImage
import java.io.File

import nutria.core.Image

object Image {
  def buffer(image: Image): BufferedImage = {
    val b = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for {
      w <- 0 until image.width
      h <- 0 until image.height
    } b.setRGB(w, h, image(w, h).hex)
    b
  }

  def save(image: Image, file: java.io.File): File = {
    if (file.getParentFile != null)
      file.getParentFile.mkdirs()
    javax.imageio.ImageIO.write(buffer(image), "png", file)
    file
  }

  def verboseSave(image: Image, file: java.io.File): File = {
    save(image, file)
    println("Saved: " + file.getAbsoluteFile.toString)
    file
  }
}
