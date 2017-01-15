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

package nutria.core

import java.awt.image.BufferedImage
import java.io.File

import nutria.core.accumulator.{Accumulator, Arithmetic}
import nutria.core.colors.{Invert, Periodic, Wikipedia}
import nutria.core.content._

object syntax {

  implicit class EnrichedViewport(val viewport: Viewport) extends AnyVal {
    def withDimensions(dim: Dimensions): Transform = new Transform(viewport, dim)
  }

  implicit class EnrichedTransform(val transform: Transform) extends AnyVal {
    def withFractal[B](fractal: ContentFunction[B]): Content[B] = FractalContent(fractal, transform)

    def withAntiAliasedFractal(fractal: ContentFunction[Double], accu: Accumulator = Arithmetic, samplingFactor: Int = 2): Content[Double] =
      AntiAliasedFractalContent(fractal, transform, accu, samplingFactor)

//    def withBuddhaBrot(sourceTransform: Transform = transform, maxIteration: Int = 250) =
//      BuddahBrot(transform, sourceTransform, maxIteration)
  }

  implicit class EnrichedContentFunctions[A, B](val content: ContentFunction[A]) extends AnyVal {
    def map(f: A => B): ContentFunction[B] = new ContentFunctionMap(content, f)
    def ~>(f: A => B): ContentFunction[B] = map(f)
  }

  implicit class EnrichedContentForCache[A](val content: Content[A]) extends AnyVal {
    def cached: CachedContent[A] = content match {
      case cached: CachedContent[A] => cached
      case _ => new CachedContent[A](content)
    }
  }

  implicit class EnrichedContentForLinNorm(val content: Content[Double]) extends AnyVal {
    def linearNormalized(min:Double, max:Double): NormalizedContent[Double] = LinearNormalizedContent(content.cached, min, max)
    def linearNormalized: NormalizedContent[Double] = LinearNormalizedContent.automatic(content.cached)
  }

  implicit class EnrichedContentForStrNorm[A](val content: Content[A]) extends AnyVal {
    def strongNormalized(implicit ordering: Ordering[A]): NormalizedContent[Double] = StrongNormalizedContent(content.cached)
  }

  implicit class EnrichedFinishedContent[A](val content: NormalizedContent[A]) extends AnyVal {
    def withColor(color: Color[A]) = Image(content, color)
  }

  implicit class EnrichColor(val color: Color[Double]) extends AnyVal {
    def invert:Color[Double] = Invert.invert(color)
    def repeated(repeat:Int) = Periodic(color, 0d, repeat)
  }

  implicit class EnrichedFinishedContentWithDefaultColors(val content: NormalizedContent[Double]) extends AnyVal {
    def withDefaultColor = Image(content, Wikipedia)
    def withInvertDefaultColor = Image(content, Wikipedia.invert)
  }

  implicit class EnrichedImage(val image: Image) extends AnyVal {
    def buffer: BufferedImage = Image.buffer(image)
    def save(file: java.io.File): File = Image.save(image, file)
    def verboseSave(file: java.io.File): File = Image.verboseSave(image, file)
  }
}