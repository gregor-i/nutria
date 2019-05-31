import DefaultSaveFolder.defaultSaveFolder
import nutria.core.colors.Invert
import nutria.core.content.{Content, LinearNormalized}
import nutria.core.image.SaveFolder
import nutria.core.syntax._
import nutria.core.viewport.{Dimensions, Viewport}
import nutria.data.Defaults
import nutria.data.colors.MonoColor
import nutria.data.consumers.CountIterations
import nutria.data.sequences.JuliaSet
import processorHelper.{ProcessorHelper, Task}
object JuliaSetMap extends ProcessorHelper with Defaults {
  val saveFolder: SaveFolder = defaultSaveFolder / "JuliaSetMap"

  override def statusPrints: Boolean = true

  val colors = Seq(MonoColor.Blue, Invert(MonoColor.Blue))
  val view = Viewport.createViewportByLongs(0xbffcccccccccccccL, 0xbff0000000000000L, 0x400c000000000000L, 0x0L, 0x0L, 0x4000000000000000L)


  object JuliaSetMapTask extends Task {
    val file = saveFolder /~ "map.png"

    override def name = "JuliaSetMapTask"

    override def skipCondition: Boolean = file.exists()

    val patchDimensions = defaultDimensions.scale(0.1)

    def content(cx: Double, cy: Double):Content[Double] = {
      view
        .withDimensions(patchDimensions)
        .withContent(new JuliaSet(cx, cy)(5000) andThen CountIterations.smoothed())
    }

    override def execute(): Unit = {
      val patches = (for {
        i <- -10 to 10
        j <- -10 to 10
        x = 0.1 * i
        y = 0.1 * j
      } yield (i+10, j+10) -> content(x, y)).toMap

      val combined = new Content[Double]{
        override val dimensions: Dimensions = patchDimensions.scale(21)

        override def apply(x: Int, y: Int): Double = {
          val patchX = x / patchDimensions.width
          val patchY = y / patchDimensions.height
          val inPatchX = x % patchDimensions.width
          val inPatchY = y % patchDimensions.height

          patches(patchX, patchY).apply(inPatchX, inPatchY)
        }
      }

      combined.map(LinearNormalized(0d, 5000d)).withColor(MonoColor.Blue).save(file)
    }
  }


  case class JuliaSetTask(cx: Double, cy: Double) extends Task {
    val file = saveFolder /~ s"$cx,$cy.png"

    override def name = s"JuliaSetTask($cx, $cy)"

    override def skipCondition: Boolean = file.exists()

    override def execute(): Unit = {
      view
        .withDimensions(Defaults.defaultDimensions.scale(0.1))
        .withContent(new JuliaSet(cx, cy)(500) andThen CountIterations.double() andThen LinearNormalized(0d, 500d))
        .withColor(MonoColor.Blue).save(file)
    }
  }

  def main(args: Array[String]): Unit = {
//    executeAllTasks(
//      for {
//        i <- -10 to 10
//        j <- -10 to 10
//        x = 0.1 * i
//        y = 0.1 * j
//      } yield JuliaSetTask(x, y)
//    )

    executeAllTasks(Seq(JuliaSetMapTask))
  }
}
