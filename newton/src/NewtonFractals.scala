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

import nurtia.data.consumers.NewtonColoring
import nurtia.data.fractalFamilies.MandelbrotData
import nurtia.data.sequences.Newton
import nurtia.data.{Defaults, DimensionInstances}
import nutria.core.syntax._
import processorHelper.ProcessorHelper
import spire.math.Complex

import scala.io.Source
import scala.util.Try

object NewtonFractals extends ProcessorHelper with Defaults {
  type C = Complex[Double]
  type F = C => C

  private object ParseAndDerive {
    import mathParser.Parser
    import mathParser.implicits._

    val x = 'x
    val lang = mathParser.complex.ComplexLanguage
    val parser = Parser(lang, Set(x))
    val derive = mathParser.complex.ComplexDerive
    def eval = mathParser.Evaluate

    def apply(s: String): Option[(F, F)] =
      for {
        t1 <- parser(s)
        t2 = derive(t1)(x)
        f1 = (c: C) => eval(t1){ case 'x => c}
        f2 = (c: C) => eval(t2){ case 'x => c}
      } yield (f1, f2)
  }


  case class NewtonByString(function: String) extends Newton {
    val Some((f1, f2)) = ParseAndDerive(function)

    override def f(c: C): C = f1(c)

    override def f_der(c: C): C = f2(c)
  }

  private val saveFolder = defaultSaveFolder / "Newton"

  case class Task(fileName: String, function: String) extends processorHelper.Task {
    override def skipCondition: Boolean = (saveFolder /~ s"$fileName.png").exists()

    override def name = function

    val newton = NewtonByString(function)

    override def execute(): Unit =
      MandelbrotData.initialViewport
        .withDimensions(DimensionInstances.fullHD)
        .withFractal(newton(50) ~> NewtonColoring.smooth(newton))
        .save(saveFolder /~ s"$fileName.png")
  }


  def main(args: Array[String]): Unit = {
    val source = Source.fromFile("newton/newton.fractals")
    val tasks = source.getLines().flatMap { line =>
      Try(line.split(" -> ").take(2))
        .map(l => Task(l(0), l(1)))
        .toOption
    }.toSet

    executeAllTasks(tasks)
  }
}