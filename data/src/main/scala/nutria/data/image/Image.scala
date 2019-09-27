package nutria.data.image

import java.awt.image.{BufferedImage, DataBufferInt}
import java.io.{ByteArrayOutputStream, File}

import nutria.core.RGBA

import scala.util.control.NonFatal

object Image {
  def buffer(image: nutria.data.Image, fallbackColor: RGBA): BufferedImage = {
    val b = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
    val data = b.getRaster.getDataBuffer.asInstanceOf[DataBufferInt]
    for {
      w <- 0 until image.width
      h <- 0 until image.height
    } {
      val color = try {
        image(w, h)
      } catch {
        case NonFatal(_) => fallbackColor
      }
      val hex: Int = (color.A * 255).toInt << 24 | color.R.toInt << 16 | color.G.toInt << 8 | color.B.toInt
      data.setElem(w + h * image.width, hex)
    }
    b
  }

  def bytes(image: nutria.data.Image, fallbackColor: RGBA): Array[Byte] = {
    val byteOutputStream = new ByteArrayOutputStream()
    javax.imageio.ImageIO.write(Image.buffer(image, fallbackColor), "png", byteOutputStream)
    val bytes = byteOutputStream.toByteArray
    byteOutputStream.close()
    bytes
  }

  def save(image: nutria.data.Image, fallbackColor: RGBA, file: java.io.File): File = {
    if (file.getParentFile != null)
      file.getParentFile.mkdirs()
    javax.imageio.ImageIO.write(buffer(image, fallbackColor), "png", file)
    file
  }

  def verboseSave(image: nutria.data.Image, fallbackColor: RGBA, file: java.io.File): File = {
    save(image, fallbackColor, file)
    println("Saved: " + file.getAbsoluteFile.toString)
    file
  }
}
