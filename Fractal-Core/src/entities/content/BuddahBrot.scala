package entities
package content

import entities.fractal.Mandelbrot
import entities.viewport.HasDimensions

private object BuddahBrotHelper{
  def ignoreIndex(operation : => Unit) = {
    try{
      val u:Unit = operation
    }catch{
      case _:ArrayIndexOutOfBoundsException =>
    }
  }
}

case class BuddahBrot(targetViewport: Transform, sourceViewport: Transform, maxIterations: Int) extends Content {
  val dimensions = targetViewport.dimensions

  private val values = Array.ofDim[Double](width, height)

  def loop(sx: Double, sy: Double): Unit = {
    for ((x, y) <- new Mandelbrot.Iterator(sx, sy, maxIterations).wrapped) {
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

case class BuddahBrotWithLines(targetViewport: Transform, sourceViewport: Transform, maxIterations: Int = 250, steps: Int = 100) extends Content {
  val dimensions = targetViewport.dimensions

  private val values = Array.ofDim[Double](width, height)

  def loop(sx: Double, sy: Double): Unit = {
    val iterator = new Mandelbrot.Iterator(sx, sy, maxIterations)
    var state = (iterator.x, iterator.y)
    while(iterator.hasNext){
      val lastState = state
      state = (iterator.x, iterator.y)

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
