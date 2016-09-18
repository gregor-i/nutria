package entities

import java.io.File

import entities.accumulator.Arithmetic
import entities.content._

object syntax {

  implicit class EnrichedViewport(val viewport: Viewport) extends AnyVal {
    def withDimensions(dim: Dimensions): Transform = new Transform(viewport, dim)
  }

  implicit class EnrichedTransform(val transform: Transform) extends AnyVal {
    def withFractal(fractal: Fractal): Content = FractalContent(fractal, transform)

    def withAntiAliasedFractal(fractal: Fractal, accu: Accumulator = Arithmetic, samplingFactor: Int = 5): Content =
      AntiAliasedFractalContent(fractal, transform, accu, samplingFactor)

    def withDynamAntiAliasedFractal(fractal: Fractal, accu: Accumulator = Arithmetic,
                                    limit: Double = 0.12, minimalIterations: Int = 4, maximalIterations: Int = 50): Content =
      DynamAntiAliasFractalContent(fractal, transform, accu, limit, minimalIterations, maximalIterations)


    def withBuddhaBrot(sourceTransform: Transform = transform, maxIteration: Int = 250) =
      BuddahBrot(transform, sourceTransform, maxIteration)
  }

  implicit class EnrichedContent(val content: Content) extends AnyVal {
    def cached: CachedContent = content match {
      case cached: CachedContent => cached
      case _ => new CachedContent(content)
    }

    def linearNormalized: FinishedContent = LinearNormalizedContent(cached)
    def strongNormalized: FinishedContent = StrongNormalizedContent(cached)
  }

  implicit class EnrichedFinishedContent(val content: FinishedContent) extends AnyVal {
    def withColor(color: Color) = new Image(content, color)
  }

  implicit class EnrichedImage(val image: Image) extends AnyVal {
    def save(file: java.io.File): File = {
      if (file.getParentFile != null)
        file.getParentFile.mkdirs()
      javax.imageio.ImageIO.write(image.buffer, "png", file)
      file
    }

    def verboseSave(fileName: String): File = {
      val file = new java.io.File(fileName)
      save(file)
      println("Saved: " + file.getAbsoluteFile)
      file
    }

    def save(fileName: String): Unit = save(new java.io.File(fileName))
  }

  implicit class EnrichmentFanOut[A](val self:A) extends AnyVal{
    def fanOut[B](ops: (A => B)*):Seq[B] = ops.map(_.apply(self))
  }
}