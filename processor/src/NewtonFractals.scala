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
import nutria.core.consumers.DirectColors
import nutria.core.image.DefaultSaveFolder
import nutria.core.sequences.{ExperimentalNewton, Newton, SimplePolynom, ThreeRoots}
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.ProcessorHelper
import spire.implicits._
import spire.math.Complex

object NewtonFractals extends ProcessorHelper {
  type C = Complex[Double]
  type F = C => C

  class NewtonByFunctions(val f1: F, val f2: F) extends Newton {
    override def f(c: C): C = f1(c)

    override def f_der(c: C): C = f2(c)
  }

  override def statusPrints: Boolean = true

  val saveFolder = DefaultSaveFolder / "Newton"

  case class Task(name: String, functions: Newton) extends processorHelper.Task {
    override def skipCondition: Boolean = (saveFolder /~ s"$name.png").exists()

    override def execute(): Unit =
      MandelbrotData.initialViewport
        .withDimensions(Dimensions.fullHD)
        .withFractal(functions(50) ~> DirectColors())
        .save(saveFolder /~ s"$name.png")
  }

  def someRandomPolynoms(n:Int) =
    for{
      _ <- 0 until n
      a = Math.random()*20-10
      b = Math.random()*20-10
      c = Math.random()*20-10
      d = Math.random()*20-10
    } yield Task(s"SimplePolynom($a + $b i, $c + $d i)", new SimplePolynom(Complex(a, b), Complex(c, d)))

  def main(args: Array[String]): Unit = {
    val tasks = Set(
      Task("ThreeRoots", ThreeRoots),
      Task("ExperimentalNewton", ExperimentalNewton),
      Task("SimplePolynom(3+2i, 2)", new SimplePolynom(Complex(3, 2), 2)),
      Task("SimplePolynom(10, 2)", new SimplePolynom(10, 2)),
      Task("SimplePolynom(1+2i, 2)", new SimplePolynom(Complex(1, 2), 2)),
      Task("SimplePolynom(1-2i, 2)", new SimplePolynom(Complex(1, -2), 2)),
      Task("SimplePolynom(-1-2i, 2)", new SimplePolynom(Complex(-1, -2), 2)),
      Task("sin", new NewtonByFunctions(c => c.sin + 0.1, c => c.cos)),
      Task("tan", new NewtonByFunctions(c => c.tan + 0.1, c => (1/c.cos) ** 2)),
      Task("exp", new NewtonByFunctions(c => c.exp-5, c => c.exp)),
      Task("-exp", new NewtonByFunctions(c => (-c).exp-5, c => -((-c).exp))),
      Task("Spektrum", new NewtonByFunctions(c => 0, c => 1)),
      Task("Z1", new NewtonByFunctions(c => c/(c-1) + 1, c => 1/(c-1)**2)),
      Task("x^x", new NewtonByFunctions(c => c**c-1, c => c**c*(c.log + 1))),
      Task("x sin(x)", new NewtonByFunctions(c => c.sin*c, c => c.sin+c.cos*c))
    )

    executeAllTasks(tasks)
  }
}