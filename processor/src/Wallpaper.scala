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

import nutria.core.Viewport
import nutria.core.accumulator.Max
import nutria.core.consumers._
import nutria.core.content.Content
import nutria.core.image.{DefaultSaveFolder, SaveFolder}
import nutria.core.sequences.Mandelbrot
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.{ProcessorHelper, Task}
import viewportSelections.ViewportSelection

object Wallpaper extends ProcessorHelper {
  case class WallpaperTask(view: Viewport) extends Task {
    val saveFolder: SaveFolder = DefaultSaveFolder / "Wallpaper" / s"$view"

    override def name: String = s"WallpaperTask($view)"

    override def skipCondition: Boolean = (saveFolder /~ "added.png").exists()

    override def execute(): Unit = {
      val transform = view
        .withDimensions(Dimensions.fullHD)

      val escape = transform
        .withAntiAliasedFractal(Mandelbrot(5000, 20d) ~> SmoothColoring()).strongNormalized

      val smallestStep = transform
        .withAntiAliasedFractal(Mandelbrot(7500, 20d) ~> SmallestStep(), Max).strongNormalized

      val added = new Content[Double] {
        override def dimensions: Dimensions = Dimensions.fullHD
        override def apply(x: Int, y: Int): Double = escape(x, y) + smallestStep(x, y)
      }.strongNormalized

      escape.withDefaultColor.save(saveFolder /~ "escape.png")
      smallestStep.withDefaultColor.save(saveFolder /~ "smallestStep.png")
      added.withDefaultColor.save(saveFolder /~ "added.png")
    }

  }

  def main(args: Array[String]): Unit = {
    executeAllTasks(
      for (view <- ViewportSelection.selection)
        yield WallpaperTask(view))
  }
}
