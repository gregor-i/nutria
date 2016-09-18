import java.io.{File, FileWriter}

import nutria.fractal.{Mandelbrot, QuatBrot}
import nutria.syntax._
import nutria.viewport.Dimensions
import spire.math.Quaternion

object Matlab3DExport extends App {
  val dimensions = Dimensions.fullHD.scale(0.1)
  val viewport = Mandelbrot.start
  val transform =  viewport.withDimensions(dimensions)

  val iterations = 50

  val writer = new FileWriter(new File("/home/gregor/Matlab3DExport.m"))

  writer.append(s"data = zeros(${dimensions.width}, ${dimensions.height}, ${dimensions.height/2});\n")

  for{
    i <- 0 until dimensions.height / 2
  } {
    def p(i:Int) = transform.transformY(0, i)
    val content = transform
      .withFractal(new QuatBrot({ case (x, y) => Quaternion(x, y, p(i), p(i))}).RoughColoring(iterations))
      .cached

    val data = s"data(:, :, ${i+1}) = [${content.values.map(_.mkString(",")).mkString(";")}];\n"
    println(data.length)

    writer.append(data)
    println(s" $i / ${dimensions.height/2}")
  }

  writer.flush()
  writer.close()
}
