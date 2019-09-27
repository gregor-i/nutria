import nutria.core.RGBA
import nutria.data.Defaults
import nutria.data.consumers.NewtonColoring
import nutria.data.fractalFamilies.MandelbrotFamily
import nutria.data.image.Image
import nutria.data.sequences.NewtonFractalByString
import nutria.data.syntax._
import processorHelper.ProcessorHelper
import spire.math.Complex

import scala.io.Source

object NewtonFractalsByFile extends ProcessorHelper with Defaults {
  type C = Complex[Double]
  type F = (C, C) => C

  private val saveFolder = defaultSaveFolder / "Newton"

  case class Task(fileName: String, function: String, c0Function: String) extends processorHelper.Task {
    override def skipCondition: Boolean = (saveFolder /~ s"$fileName.png").exists()

    override def name = function

    val newton = NewtonFractalByString(function, c0Function)

    override def execute(): Unit =
      Image.save(
        MandelbrotFamily.initialViewport
          .withDimensions(Defaults.defaultDimensions)
          .withContent(newton(50) andThen NewtonColoring.smooth(newton))
          .cached, RGBA.white, saveFolder /~ s"$fileName.png")
  }


  val reqExWithoutStartPoint = "^(.*) -> (.*)$".r
  val regExWithStartPoint = "^(.*) -> (.*), (.*)$".r
  val regExComment = "^#.*".r

  def main(args: Array[String]): Unit = {
    val source = Source.fromFile("newton.fractals")
    val tasks = source.getLines().flatMap {
      case regExComment() => None
      case regExWithStartPoint(name, f, c0) => Some(Task(name, f, c0))
      case reqExWithoutStartPoint(name, f) => Some(Task(name, f, "lambda"))
      case _ => None
    }.toSet

    executeAllTasks(tasks)
  }
}
