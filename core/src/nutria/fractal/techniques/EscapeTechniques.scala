package nutria
package fractal.techniques

import nutria.fractal.{AbstractSequence, SequenceConstructor}

object EscapeTechniques{
  def apply[A <: AbstractSequence:SequenceConstructor]: EscapeTechniques[A] = new EscapeTechniques[A]
}

class EscapeTechniques[A <: AbstractSequence](implicit sequence: SequenceConstructor[A]) {
  def RoughColoring(maxIteration: Int): Fractal = sequence(_, _, maxIteration).size().toDouble

  def Brot(maxIteration: Int): Fractal =
    (x, y) => if (sequence(x, y, maxIteration).size() == maxIteration) 1 else 0
}
