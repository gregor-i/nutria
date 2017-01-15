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

package nurtia.data.content

import nurtia.data.sequences.Mandelbrot
import nutria.core.Transform
import nutria.core.content.Content

private object BuddahBrotHelper{
  def ignoreIndex(operation : => Unit) = {
    try{
      val u:Unit = operation
    }catch{
      case _:ArrayIndexOutOfBoundsException =>
    }
  }
}

case class BuddahBrot(targetViewport: Transform, sourceViewport: Transform, maxIterations: Int) extends Content[Double] {
  val dimensions = targetViewport.dimensions

  private val values = Array.ofDim[Double](width, height)

  def loop(sx: Double, sy: Double): Unit = {
    for ((x, y) <- new Mandelbrot.Sequence(sx, sy, maxIterations, 4).wrapped) {
      val ix = targetViewport.invertX(x, y)
      val iy = targetViewport.invertY(x, y)
      if (!ix.isNaN && !iy.isNaN){
        BuddahBrotHelper.ignoreIndex(values(ix.toInt)  (iy.toInt)   += 0 + ix % 1 + iy % 1)
        BuddahBrotHelper.ignoreIndex(values(ix.toInt+1)(iy.toInt)   += 1 - ix % 1 + iy % 1)
        BuddahBrotHelper.ignoreIndex(values(ix.toInt)  (iy.toInt+1) += 1 + ix % 1 - iy % 1)
        BuddahBrotHelper.ignoreIndex(values(ix.toInt+1)(iy.toInt+1) += 2 - ix % 1 - iy % 1)
      }
    }
  }

  for (sx <- 0 until sourceViewport.width;
       sy <- (0 until sourceViewport.height).par)
    loop(sourceViewport.transformX(sx, sy), sourceViewport.transformY(sx, sy))

  def apply(x: Int, y: Int): Double = values(x)(y)
}

case class BuddahBrotWithLines(targetViewport: Transform, sourceViewport: Transform, maxIterations: Int = 250, steps: Int = 100) extends Content[Double] {
  val dimensions = targetViewport.dimensions

  private val values = Array.ofDim[Double](width, height)

  def loop(sx: Double, sy: Double): Unit = {
    val iterator = Mandelbrot(maxIterations, 2d)(sx, sy)
    var state = iterator.public
    while(iterator.hasNext){
      val lastState = state
      iterator.next()
      state = iterator.public

      for(f <- 0d until 1d by (1d/steps)){
        val x = lastState._1 * f + state._1 *(1-f)
        val y = lastState._2 * f + state._2 *(1-f)

        val ix = targetViewport.invertX(x, y)
        val iy = targetViewport.invertY(x, y)
        if (!ix.isNaN && !iy.isNaN && ix > 0 && iy > 0 && ix < width && iy < height){
          BuddahBrotHelper.ignoreIndex(values(ix.toInt)  (iy.toInt)   += 0 + ix % 1 + iy % 1)
          BuddahBrotHelper.ignoreIndex(values(ix.toInt+1)(iy.toInt)   += 1 - ix % 1 + iy % 1)
          BuddahBrotHelper.ignoreIndex(values(ix.toInt)  (iy.toInt+1) += 1 + ix % 1 - iy % 1)
          BuddahBrotHelper.ignoreIndex(values(ix.toInt+1)(iy.toInt+1) += 2 - ix % 1 - iy % 1)
        }
      }
    }
  }

  for (sx <- 0 until sourceViewport.width;
       sy <- (0 until sourceViewport.height).par) {
    loop(sourceViewport.transformX(sx, sy), sourceViewport.transformY(sx, sy))
  }

  def apply(x: Int, y: Int): Double = values(x)(y)
}
