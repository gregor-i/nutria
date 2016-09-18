package nutria
package fractal.technics

import nutria.fractal.sequence.{HasSequenceConstructor, Sequence}

trait EscapeTechnics[A <: Sequence] {
  _: HasSequenceConstructor[A] =>

  def RoughColoring(maxIteration: Int): Fractal = sequence(_, _, maxIteration).size().toDouble


  def Brot(maxIteration: Int): Fractal =
    (x, y) => if (sequence(x, y, maxIteration).size() == maxIteration) 1 else 0
}
