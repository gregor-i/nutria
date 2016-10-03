/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nutria

import java.io.File

import nutria.accumulator.Arithmetic
import nutria.color.{HSV, Invert}
import nutria.content._

object syntax {

  implicit class EnrichedViewport(val viewport: Viewport) extends AnyVal {
    def withDimensions(dim: Dimensions): Transform = new Transform(viewport, dim)
  }

  implicit class EnrichedTransform(val transform: Transform) extends AnyVal {
    def withFractal(fractal: Fractal): Content = FractalContent(fractal, transform)

    def withAntiAliasedFractal(fractal: Fractal, accu: Accumulator = Arithmetic, samplingFactor: Int = 5): Content =
      AntiAliasedFractalContent(fractal, transform, accu, samplingFactor)

    def withBuddhaBrot(sourceTransform: Transform = transform, maxIteration: Int = 250) =
      BuddahBrot(transform, sourceTransform, maxIteration)
  }

  implicit class EnrichedSequenceConstructors[A <: AbstractSequence](val constructor: SequenceConstructor[A]) extends AnyVal {
    def withConsumer(consumer: SequenceConsumer[A]): Fractal = (x, y) => consumer(constructor(x, y))
    def ~>(consumer: SequenceConsumer[A]): Fractal = withConsumer(consumer)
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
    def withDefaultColor = new Image(content, HSV.MonoColor.Blue)
    def withInvertDefaultColor = new Image(content, Invert(HSV.MonoColor.Blue))
  }

  implicit class EnrichedImage(val image: Image) extends AnyVal {
    def save(file: java.io.File): File = {
      if (file.getParentFile != null)
        file.getParentFile.mkdirs()
      javax.imageio.ImageIO.write(image.buffer, "png", file)
      file
    }

    def save(fileName: String): Unit = save(new java.io.File(fileName))

    def verboseSave(file: java.io.File): File = {
      save(file)
      println("Saved: " + file.getAbsoluteFile)
      file
    }

    def verboseSave(fileName: String): File = {
      val file = new java.io.File(fileName)
      verboseSave(file)
    }
  }

  implicit class EnrichmentFanOut[A](val self:A) extends AnyVal{
    def fanOut[B](ops: (A => B)*):Seq[B] = ops.map(_.apply(self))
  }

  implicit class FoldBoooleans(val self: Boolean) extends AnyVal {
    def fold[B](ifTrue: => B, ifFalse: => B): B = if (self) ifTrue else ifFalse
  }
}