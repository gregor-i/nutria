import nutria.color.{HSV, Invert}
import nutria.content.CachedContent
import nutria.fractal.Mandelbrot
import nutria.fractal.sequence.MandelbrotSequence
import nutria.fractal.techniques.CardioidTechniques
import nutria.syntax._
import nutria.viewport.Dimensions


/**
  * Saves a sequence of Files. The sequence shows how the cardioid images are constructed. Each image is the state at the defined iteration.
  */
object CardioidIterations extends ProcessorHelper {
  override def rootFolder: String = "/home/gregor/Pictures/CardioidIterations/"

  override def statusPrints: Boolean = true

  def calculateDistance(seq: MandelbrotSequence): Double = {
    if (seq.hasNext)
      CardioidTechniques.minimalDistance(1 to 100)(seq.publicX, seq.publicY)
    else
      2
  }


  def main(args: Array[String]): Unit = {
    val dim = Dimensions.fullHD

    val transform = Mandelbrot.start.withDimensions(dim)

    val seqs = for (i <- 0 until dim.width)
      yield for {
        j <- 0 until dim.height
        (x, y) = transform(i, j)
      } yield Mandelbrot.sequence(x, y, 1000)

    var oldContent = new CachedContent(seqs.par.map(_.map(calculateDistance)).seq, dim)

    for (i <- 0 until 1000) {
      seqs.par.foreach(_.foreach(_.next()))
      val nextContent =
        new CachedContent(
          (for (i <- (0 until dim.width).par)
            yield for {
              j <- 0 until dim.height
            } yield calculateDistance(seqs(i)(j)).min(oldContent(i, j))).seq
          , dim)
      nextContent.strongNormalized.withColor(Invert(HSV.MonoColor.Blue)).verboseSave(fileInRootFolder(s"iteration_$i.png"))
      oldContent = nextContent
    }
  }
}
