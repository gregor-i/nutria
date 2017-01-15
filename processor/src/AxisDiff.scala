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

import nurtia.data.Defaults
import nurtia.data.consumers.{OrbitImgAxis, OrbitRealAxis}
import nurtia.data.fractalFamilies.MandelbrotData
import nurtia.data.sequences.Mandelbrot
import nutria.core.Viewport
import nutria.core.accumulator.Variance
import nutria.core.syntax._
import processorHelper.ProcessorHelper

object AxisDiff extends ProcessorHelper with Defaults {
  override def statusPrints: Boolean = true

  object Fractal extends nutria.core.ContentFunction[Double] {
    val sequence = Mandelbrot(350, 100d)
    val real = sequence ~> OrbitRealAxis()
    val imag = sequence ~> OrbitImgAxis()

    def apply(x: Double, y: Double): Double = real(x, y) - imag(x, y)
  }

  val saveFolder = defaultSaveFolder / "AxisDiff"

  case class Task(viewport: Viewport) extends processorHelper.Task {
    override def name: String = toString

    override def skipCondition: Boolean = (saveFolder /~ s"$viewport.png").exists()

    override def execute(): Unit = {
      val diff = viewport
        .withDimensions(default)
        .withAntiAliasedFractal(Fractal, Variance)
        .strongNormalized

      diff
        .withColor(default)
        .save(saveFolder /~ s"${viewport}_invert.png")

      diff
        .withColor(defaultColor.invert)
        .save(saveFolder /~ s"$viewport.png")
    }
  }

  def main(args: Array[String]): Unit = {
    val tasks1: Set[Task] = Set(Task(MandelbrotData.initialViewport))
    val tasks2: Set[Task] = MandelbrotData.selectionViewports.map(Task)
    val tasks3: Set[Task] = MandelbrotData.Focus.iteration2.map(Task)

    executeAllTasks(tasks1.toSeq)
    executeAllTasks(tasks3.toSeq)
    executeAllTasks(tasks2.toSeq)
  }
}