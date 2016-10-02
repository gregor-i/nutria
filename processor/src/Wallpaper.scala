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

import nutria.Color
import nutria.accumulator.Max
import nutria.color.HSV
import nutria.consumers.{CircleP2, RoughColoring}
import nutria.content.Content
import nutria.sequences.Mandelbrot
import nutria.syntax._
import nutria.viewport.{Dimensions, Viewport}
import viewportSelections.ViewportSelection

object Wallpaper extends ProcessorHelper {
  override def rootFolder: String = "/home/gregor/Pictures/Wallpaper/"

  override def statusPrints: Boolean = true

  case class WallpaperTask(view: Viewport, color: Color) extends Task {
    override def name: String = s"WallpaperTask($view)"

    override def skipCondition: Boolean = fileInRootFolder(s"$view/added.png").exists()

    override def execute(): Unit = {
      val transform = view
        .withDimensions(Dimensions.fullHD)

      val rough = transform
        .withAntiAliasedFractal(Mandelbrot(5000) ~> RoughColoring()).strongNormalized

      val circle = transform
        .withAntiAliasedFractal(Mandelbrot(7500) ~>CircleP2(), Max).strongNormalized

      val added = new Content {
        override def dimensions: Dimensions = Dimensions.fullHD

        override def apply(x: Int, y: Int): Double = rough(x, y) + circle(x, y)
      }.strongNormalized

      rough.withColor(color).save(fileInRootFolder(s"$view/rough.png"))
      circle.withColor(color).save(fileInRootFolder(s"$view/circle.png"))
      added.withColor(color).save(fileInRootFolder(s"$view/added.png"))
    }

    def main(args: Array[String]): Unit = {
      executeAllTasks(
        for (view <- ViewportSelection.selection)
          yield WallpaperTask(view, HSV.MonoColor.Blue))
    }
  }

}
