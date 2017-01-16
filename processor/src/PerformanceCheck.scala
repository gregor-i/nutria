import java.io.{File, PrintStream}

import nurtia.data.Defaults
import nurtia.data.optimizations.MandelbrotOptimizations
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.{NoSkip, ProcessorHelper}

object PerformanceCheck extends ProcessorHelper with Defaults {

  val dim = Dimensions(16, 9)

  case class Task(n: Int) extends processorHelper.Task with NoSkip {
    def name: String = toString

    def execute(): Unit =
      defaultViewport
        .withDimensions(dim.scale(n))
        .withFractal(MandelbrotOptimizations.SmoothColoring(100, 100))
        .strongNormalized
  }


  def main(args: Array[String]): Unit = {
    System.setOut(new PrintStream(new File("PerformanceCheck.log")))
    executeAllTasks((1 to 1000).map(Task))
  }
}
