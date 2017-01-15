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

import nurtia.data.DimensionInstances
import nurtia.data.consumers.{OrbitPoint, SmallestStep}
import nurtia.data.sequences.Mandelbrot
import nutria.core.image.DefaultSaveFolder
import nutria.core.syntax._
import nutria.core.{ContentFunction, Viewport}
import processorHelper.ProcessorHelper

object CompSmallestStepAndOrbitTrap extends ProcessorHelper {
  val view = Viewport.createViewportByLongs(0xbfed3fc8cfd68914L, 0x3fd153b629fa6027L, 0x3f6677f4689b7037L, 0x0L, 0x0L, 0x3f59ada99c1f5babL)

  val saveFolder = DefaultSaveFolder / "CompSmallestStepAndOrbitTrap"


  override def statusPrints: Boolean = true

  case class Task(content: ContentFunction[Double], name: String) extends processorHelper.Task {
    override def skipCondition: Boolean = (saveFolder /~ s"$name.png").exists()

    override def execute(): Unit =
      view
        .withDimensions(DimensionInstances.fujitsu)
        .withFractal(content)
        .strongNormalized
        .withDefaultColor
        .verboseSave(saveFolder /~ s"$name.png")
  }

  def seq = Mandelbrot(350, 10d)

  def main(args: Array[String]): Unit = {
    val a = Task(seq ~> SmallestStep(), "SmallestStep.png")
    val bs = for {
      x <- -1 to 1
      y <- -1 to 1
    } yield Task(seq ~> OrbitPoint(x, y), s"OrbitPoint($x, $y).png")

    executeAllTasks(a +: bs)
  }
}

