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
import nurtia.data.colors.{MonoColor, RGBGradient, Wikipedia}
import nutria.core.Color
import nutria.core.content.Spectrum
import nutria.core.image.SaveFolder
import nutria.core.syntax._
import processorHelper.{ProcessorHelper, Task}

object Colors extends ProcessorHelper with Defaults {
  val saveFolder: SaveFolder = defaultSaveFolder / "ColorTest"

  case class ColorTask(color: Color[Double], name: String) extends Task {
    override def skipCondition: Boolean = false

    override def execute(): Unit =
      Spectrum.withColor(color).save(saveFolder /~ s"$name.png")
  }

  def main(args: Array[String]): Unit = {
    val tasks = Seq(
      ColorTask(Wikipedia, "Wikipedia"),
      ColorTask(RGBGradient.default, "RGB.default"),
      ColorTask(MonoColor.Blue, "HSV.MonoColor.Blue")
    )

    executeAllTasks(tasks)
  }
}
