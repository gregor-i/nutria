package nutria.staticRenderer

import java.io.{File, FileWriter}
import nutria.core.{Dimensions, FractalImage, IntParameter}
import nutria.shaderBuilder.FragmentShaderSource

import scala.sys.process._
import scala.util.{Failure, Success, Try, Using}

object Renderer {
  def renderToFile(fractalImage: FractalImage, dimensions: Dimensions, fileName: String): Try[Unit] = {
    val fragFile = File.createTempFile("nutria", ".frag")

    Using(new FileWriter(fragFile)) { fw =>
      fw.append(FragmentShaderSource.forImage(fractalImage))
      fw.close()
    }

    new File(fileName).getParentFile.mkdirs()

    val responseCode = s"""
       glslViewer ${fragFile.getAbsolutePath}
        -e u_view_O,${fractalImage.viewport.origin._1},${fractalImage.viewport.origin._2}
        -e u_view_A,${fractalImage.viewport.A._1},${fractalImage.viewport.A._2}
        -e u_view_B,${fractalImage.viewport.B._1},${fractalImage.viewport.B._2}
        -E screenshot,${fileName}
        -w ${dimensions.width}
        -h ${dimensions.height}
        --headless
        """
      .filter(_ != '\n')
      .!

    fragFile.delete()

    if (responseCode == 0)
      Success(())
    else
      Failure(new Exception(s"response code not 0, but ${responseCode}"))
  }
}
