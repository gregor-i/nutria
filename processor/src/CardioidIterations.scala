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

import nurtia.data.MandelbrotData
import nutria.core.consumers.CardioidNumeric
import nutria.core.content.CachedContent
import nutria.core.image.DefaultSaveFolder
import nutria.core.sequences.Mandelbrot
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.ProcessorHelper


/**
  * Saves a sequence of Files. The sequence shows how the cardioid images are constructed. Each image is the state at the defined iteration.
  */
object CardioidIterations extends ProcessorHelper {
  override def statusPrints: Boolean = true

  def calculateDistance(seq: Mandelbrot.Sequence): Double =
    CardioidNumeric.minimalDistance(100)(seq.publicX, seq.publicY)

  def main(args: Array[String]): Unit = {
    val dim = Dimensions.fullHD

    val transform = MandelbrotData.initialViewport.withDimensions(dim)

    val seqs = for (i <- 0 until dim.width)
      yield for {
        j <- 0 until dim.height
        (x, y) = transform(i, j)
      } yield Mandelbrot(1000, 2d)(x, y)

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
      nextContent
        .strongNormalized
        .withInvertDefaultColor
        .verboseSave(DefaultSaveFolder / "CardioidIterations" /~ s"iteration_$i.png")
      oldContent = nextContent
    }
  }
}
