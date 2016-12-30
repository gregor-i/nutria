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

import java.io.{BufferedInputStream, FileWriter}

import nurtia.data.MandelbrotData
import nutria.core.consumers.DirectColors
import nutria.core.image.DefaultSaveFolder
import nutria.core.sequences.{ExperimentalNewton, Newton, SimplePolynom, ThreeRoots}
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import processorHelper.ProcessorHelper
import spire.implicits._
import spire.math.Complex

import scala.io.Source
import scala.util.Try

object NewtonFractalsByStrings extends ProcessorHelper {
  type C = Complex[Double]
  type F = C => C

  class NewtonByFunctions(val f1: F, val f2: F) extends Newton {
    override def f(c: C): C = f1(c)

    override def f_der(c: C): C = f2(c)
  }

  override def statusPrints: Boolean = true

  val saveFolder = DefaultSaveFolder / "Newton"

  case class Task(f: String, name:String) extends processorHelper.Task {
    override def skipCondition: Boolean = (saveFolder /~ s"$name.png").exists()

    val Some((f1, f2)) = ComplexUtil.f1_and_f2(f)

    val functions = new NewtonByFunctions(f1, f2)

    override def execute(): Unit =
      MandelbrotData.initialViewport
        .withDimensions(Dimensions.fullHD)
        .withFractal(functions(50) ~> DirectColors())
        .cached
        .save(saveFolder /~ s"$name.png")
  }

  def main(args: Array[String]): Unit = {

    val source = Source.fromFile(saveFolder /~ "console.log")

    val tasks = source.getLines().flatMap{
      line => Try{
        val parts = line.split("->")
        val name = parts(0).trim
        val f = parts(1).trim
        Task(f, name)
      }.toOption
    }.toSeq

//    val tasks = Seq(
//      "x*x*x+5", "x*x*x*x+5", "sin(x) + (x+i)*(x+i)+5", "x^x - 0.99",
//      "cos(1/x+0.1)-0.1", "x^x*(x+2)",
//      "(x+2)*(x-0.5)*(x+i)*(x-i)", "(x+2)*(x-0.5)*(x+i)*(x-i)*(x+2)*(x-0.5)*(x+i)*(x-i)", "(x+2)*(x-0.5)*(x+i)*(x-i)*(x-0.5)*(x+i)",
//      "sin(x)*sin(i*x)", "sin(x - 1)*sin(i*x) + 0.1", "sin(x - 1)*sin(i*x) - 0.1",
//      "x^sin(x) + 1", "x^sin(x) - 1", "sin(x)^x + 1",
//      "sin(x)*cos(i*x) + 0.5", "sin(x*10)*cos(i*x*10) + 0.5", "sin(x*5)*cos(i*x*5)", "sin(i*x*5)*cos(x*5) + 0.5",
//      "e^-(x*x) + e^(-i*x*x)"
//    ).zipWithIndex.map(Task.tupled)

    executeAllTasks(tasks)
  }
}