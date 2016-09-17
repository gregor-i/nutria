package entities.fractal.technics

import entities.fractal.sequence.{HasSequenceConstructor, Sequence}

import scala.reflect.ClassTag

trait EscapeTechnics[A <: Sequence] {
  _: HasSequenceConstructor[A] =>

  case class RoughColoring(maxIteration: Int) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0, maxIteration).size()
//    override def toString = s"${implicitly[ClassTag[A]].runtimeClass.getSimpleName}.${this.getClass.getSimpleName}"
  }


  case class Brot(maxIteration: Int) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      if (sequence(x0, y0, maxIteration).size() == maxIteration) 0
      else 1
  }

}
