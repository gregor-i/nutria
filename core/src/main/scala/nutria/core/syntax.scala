package nutria.core

import java.awt.image.BufferedImage
import java.io.File

import nutria.core.accumulator.{Accumulator, Arithmetic}
import nutria.core.content._
import nutria.core.image.Image

object syntax {
  implicit class EnrichedViewport(val viewport: Viewport) extends AnyVal {
    def withDimensions(dim: Dimensions): Transform = new Transform(viewport, dim)
  }

  implicit class EnrichedTransform(val transform: Transform) extends AnyVal {
    def withContent[B](fractal: Point => B): FunctionContent[B] = FunctionContent(fractal, transform)
  }

  implicit class EnrichFractalContent(val fractalContent: FunctionContent[Double]) extends AnyVal {
    def multisampled(accu: Accumulator = Arithmetic, multi: Int = 2) = AntiAliasedFractalContent(fractalContent, multi, accu)
  }

  implicit class EnrichedContentForLinNorm(val content: Content[Double]) extends AnyVal {
    def linearNormalized: Content[Double] = LinearNormalized.automatic(content.cached)
  }

  implicit class EnrichedContentForStrNorm[A](val content: Content[A]) extends AnyVal {
    def histogramNormalized(implicit ordering: Ordering[A]): Content[Double] = HistogramNormalized(content.cached)
  }

  implicit class EnrichedFinishedContent[A](val content: Content[A]) extends AnyVal {
    def withColor(color: Color[A]): Image = content.map(color)
  }

  implicit class EnrichedImage(val image: Image) extends AnyVal {
    def buffer: BufferedImage = Image.buffer(image)
    def save(file: java.io.File): File = Image.save(image, file)
    def verboseSave(file: java.io.File): File = Image.verboseSave(image, file)
  }
}
