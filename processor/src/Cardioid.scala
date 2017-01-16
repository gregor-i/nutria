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
import nurtia.data.colors.{MonoColor, Wikipedia}
import nurtia.data.consumers.CardioidNumeric
import nurtia.data.fractalFamilies.MandelbrotData
import nurtia.data.sequences.Mandelbrot
import nutria.core.colors.Invert
import nutria.core.image.SaveFolder
import nutria.core.syntax._
import nutria.core.{Color, Viewport}
import processorHelper.{ProcessorHelper, Task}

object Cardioid extends ProcessorHelper with Defaults {
  override def statusPrints: Boolean = true

    case class CardioidTask(view: Viewport, saveFolder: SaveFolder) extends Task {
    override def name = s"CardioidTask($view)"

      val colors = Seq(
        "HSV.MonoColor.Blue" -> MonoColor.Blue,
        "Invert(HSV.MonoColor.Blue)" -> Invert(MonoColor.Blue),
        "Wikipedia" -> Wikipedia,
        "Invert(Wikipedia)" -> Invert(Wikipedia))

    override def skipCondition: Boolean = (saveFolder / view.toString /~ s"${colors.last._1}.png").exists()

    override def execute(): Unit = {
      val content = view
        .withDimensions(default)
        .withFractal(Mandelbrot(2000, 2d) ~> CardioidNumeric(30))
        .strongNormalized

      for((name, color) <- colors)
        content.withColor(color).save(saveFolder / view.toString /~ s"$name.png")
    }
  }

  def extractColorName(color:Color[Double]):String = color.getClass.getName.split("\\.").last

  def main(args: Array[String]): Unit = {
    val saveFolder: SaveFolder = defaultSaveFolder / "Cardioid"

    val tasks1: Set[Task] = Set(CardioidTask(MandelbrotData.initialViewport, saveFolder / "start"))

    val tasks2: Set[Task] = for (viewport <- MandelbrotData.selectionViewports)
      yield CardioidTask(viewport, saveFolder / "auswahl")

    val tasks3: Set[Task] = for (viewport <- MandelbrotData.Focus.iteration2)
      yield CardioidTask(viewport, saveFolder / "fokus")

    executeAllTasks(tasks1.toSeq)
    executeAllTasks(tasks3.toSeq)
    executeAllTasks(tasks2.toSeq)
  }


  //  def checkNewton() = {
  //    val card = Mandelbrot.CardioidNumeric(50, 10)
  //
  //    val (x0, y0) = (-0.6338249269532736, 0.37772463334418416)
  //    /*for((x, y) <- new Mandelbrot.Iterator(x0, y0, 50).wrapped) {
  //      val g = card.golden(x, y)
  //      val n = card.newton(g, x, y)
  //      val m = card.minimalDistance(x, y)
  //      println(s"$x, $y, $g, ${card.dist(g, x, y)}, $n, ${card.dist(n, x, y)}")
  //    }*/
  //    for(t <- 2d until 3 by 0.001)
  //      println(s"$t, ${CardioidTechnics.dist(t, x0, y0)}, ${CardioidTechnics.d_derived(t, x0, y0)}, ${CardioidTechnics.d_derived2(t, x0, y0)}")
  //  }

  //  def checkAbls() = {
  //    val card = Mandelbrot.CardioidNumeric(50, 10)
  //    val check = (for{
  //      x <- -2d to 2d by 0.1
  //      y <- -2d to 2d by 0.1
  //      t <- -2d to 2d by 0.1
  //      d = CardioidTechnics.der1DivDer2(t, x, y)
  //      d1c = CardioidTechnics.d_derived_ana(t, x, y)
  //      d2c = CardioidTechnics.d_derived2_ana(t, x, y)
  //    } yield {
  //      val f = (d - (d1c / d2c)).abs < 1e-10
  //      if(!f)println(d - (d1c / d2c))
  //      f
  //    }).forall(identity)
  //    println(check)
  //  }

  //checkNewton()
  //  checkAbls()

  //  val card = Mandelbrot.CardioidNumeric(50, 10)
  //
  //  for {
  //    x <- -2.0 to 2.0 by 0.1
  //    y <- -2.0 to 2.0 by 0.1
  //    t <- -2.0 to 2.0 by 0.1
  //  } {
  ////    println(s"1 num: ${card.d_derived(t, x, y)}; ana: ${card.d_derived_ana(t, x, y)}")
  //   println (s"2 diff: ${card.d_derived2(t, x, y) - card.d_derived2_ana(t, x, y)}; num: ${card.d_derived2(t, x, y)}; ana: ${card.d_derived2_ana(t, x, y)}")
  //}
}
