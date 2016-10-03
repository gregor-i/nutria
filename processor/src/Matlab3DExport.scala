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

import java.io.{File, FileWriter}

import nurtia.data.MandelbrotData
import nutria.core.consumers.RoughColoring
import nutria.core.image.DefaultSaveFolder
import nutria.core.sequences.QuaternionBrot
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import spire.math.Quaternion

object Matlab3DExport extends App {
  val dimensions = Dimensions.fullHD.scale(0.1)
  val viewport = MandelbrotData.initialViewport
  val transform =  viewport.withDimensions(dimensions)

  val iterations = 50

  val writer = new FileWriter(DefaultSaveFolder /~ "Matlab3DExport.m")

  writer.append(s"data = zeros(${dimensions.width}, ${dimensions.height}, ${dimensions.height/2});\n")


  for{
    i <- 0 until dimensions.height / 2
  } {
    def p(i:Int) = transform.transformY(0, i)
    def selector(x:Double, y:Double):Quaternion[Double] = Quaternion(x, y, p(i), p(i))
    val content = transform
      .withFractal(QuaternionBrot(selector)(iterations) ~> RoughColoring())
      .cached

    val data = s"data(:, :, ${i+1}) = [${content.values.map(_.mkString(",")).mkString(";")}];\n"
    println(data.length)

    writer.append(data)
    println(s" $i / ${dimensions.height/2}")
  }

  writer.flush()
  writer.close()
}
