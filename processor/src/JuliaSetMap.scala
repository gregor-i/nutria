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

import nutria.color.{HSV, Invert}
import nutria.content.Content
import nutria.fractal.JuliaSet
import nutria.fractal.techniques.{RoughColoring, SmoothColoring}
import nutria.syntax._
import nutria.viewport.{Dimensions, Viewport}

object JuliaSetMap extends ProcessorHelper {
  override def rootFolder: String = "/home/gregor/Pictures/JuliaSetMap/"

  override def statusPrints: Boolean = true

  val colors = Seq(HSV.MonoColor.Blue, Invert(HSV.MonoColor.Blue))
  val view = Viewport.createViewportByLongs(0xbffcccccccccccccL, 0xbff0000000000000L, 0x400c000000000000L, 0x0L, 0x0L, 0x4000000000000000L)


  object JuliaSetMapTask extends Task {
    val file = fileInRootFolder(s"map.png")

    override def name = s"JuliaSetMapTask"

    override def skipCondition: Boolean = file.exists()

    val patchDimensions = Dimensions.fullHD.scale(0.1)

    def content(cx: Double, cy: Double):Content = {
      view
        .withDimensions(patchDimensions)
        .withFractal(JuliaSet(cx, cy)(5000) -> SmoothColoring())
    }

    override def execute(): Unit = {
      val patches = (for {
        i <- -10 to 10
        j <- -10 to 10
        x = 0.1 * i
        y = 0.1 * j
      } yield (i+10, j+10) -> content(x, y)).toMap

      val combined = new Content{
        override def dimensions: Dimensions = patchDimensions.scale(21)

        override def apply(x: Int, y: Int): Double = {
          val patchX = x / patchDimensions.width
          val patchY = y / patchDimensions.height
          val inPatchX = x % patchDimensions.width
          val inPatchY = y % patchDimensions.height

          patches(patchX, patchY).apply(inPatchX, inPatchY)
        }
      }

      combined.strongNormalized.withColor(HSV.MonoColor.Blue).save(file)
    }
  }


  case class JuliaSetTask(cx: Double, cy: Double) extends Task {
    val file = fileInRootFolder(s"$cx,$cy.png")

    override def name = s"JuliaSetTask($cx, $cy)"

    override def skipCondition: Boolean = file.exists()

    override def execute(): Unit = {
      view
        .withDimensions(Dimensions.fullHD.scale(0.1))
        .withFractal(JuliaSet(cx, cy)(500) -> RoughColoring())
        .strongNormalized
        .withColor(HSV.MonoColor.Blue).save(file)
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
