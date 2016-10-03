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
import nutria.core.accumulator.Variance
import nutria.core.consumers.{OrbitImgAxis, OrbitRealAxis}
import nutria.core.sequences.Mandelbrot
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.ProcessorHelper
import viewportSelections.ViewportSelection

object AxisDiff extends ProcessorHelper {
  override def rootFolder: String = "E:\\snapshots\\AxisDiff\\"

  override def statusPrints: Boolean = true

  object Fractal extends nutria.core.Fractal {
    val sequence = Mandelbrot(3500, 10000)
    val real = sequence ~> OrbitRealAxis()
    val imag = sequence ~> OrbitImgAxis()

    def apply(x: Double, y: Double): Double = real(x, y) - imag(x, y)
  }

  case class Task(viewport: Viewport) extends processorHelper.Task {
    override def name: String = toString

    override def skipCondition: Boolean = fileInRootFolder(s"$viewport.png").exists()

    override def execute(): Unit = {
      val diff = viewport
        .withDimensions(Dimensions.fullHD)
        .withAntiAliasedFractal(Fractal, Variance)
        .strongNormalized

      diff
        .withInvertDefaultColor
        .verboseSave(fileInRootFolder(s"${viewport}_invert.png"))

      diff
        .withDefaultColor
        .verboseSave(fileInRootFolder(s"$viewport.png"))
    }
  }

  def main(args: Array[String]): Unit = {
    val tasks1: Set[Task] = Set(Task(Mandelbrot.start))

    val tasks2: Set[Task] = for (viewport <- ViewportSelection.selection)
      yield new Task(viewport)

    val tasks3: Set[Task] = for (viewport <- ViewportSelection.focusIteration2)
      yield new Task(viewport)

    executeAllTasks(tasks1.toSeq)
    executeAllTasks(tasks3.toSeq)
    executeAllTasks(tasks2.toSeq)
  }
}