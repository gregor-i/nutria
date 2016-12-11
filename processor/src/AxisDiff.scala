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
import nutria.core.Viewport
import nutria.core.accumulator.Variance
import nutria.core.consumers.{OrbitImgAxis, OrbitRealAxis}
import nutria.core.image.DefaultSaveFolder
import nutria.core.sequences.Mandelbrot
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.ProcessorHelper
import viewportSelections.ViewportSelection

object AxisDiff extends ProcessorHelper {
  override def statusPrints: Boolean = true

  object Fractal extends nutria.core.ContentFunction[Double] {
    val sequence = Mandelbrot(350, 10000)
    val real = sequence ~> OrbitRealAxis()
    val imag = sequence ~> OrbitImgAxis()

    def apply(x: Double, y: Double): Double = real(x, y) - imag(x, y)
  }

  val saveFolder = DefaultSaveFolder / "AxisDiff"

  case class Task(viewport: Viewport) extends processorHelper.Task {
    override def name: String = toString

    override def skipCondition: Boolean = (saveFolder /~ s"$viewport.png").exists()

    override def execute(): Unit = {
      val diff = viewport
        .withDimensions(Dimensions.fullHD)
        .withAntiAliasedFractal(Fractal, Variance)
        .strongNormalized

      diff
        .withInvertDefaultColor
        .save(saveFolder /~ s"${viewport}_invert.png")

      diff
        .withDefaultColor
        .save(saveFolder /~ s"$viewport.png")
    }
  }

  def main(args: Array[String]): Unit = {
    val tasks1: Set[Task] = Set(Task(MandelbrotData.initialViewport))
    val tasks2: Set[Task] = ViewportSelection.selection.map(Task)
    val tasks3: Set[Task] = ViewportSelection.focusIteration2.map(Task)

    executeAllTasks(tasks1.toSeq)
    executeAllTasks(tasks3.toSeq)
    executeAllTasks(tasks2.toSeq)
  }
}