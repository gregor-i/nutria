package nurtia.data

import nutria.{Fractal, Viewport}
import nutria.fractal.AbstractSequence
import simulacrum.typeclass

@typeclass trait Data[A <: AbstractSequence] {
  type Named[B] = (String, B)

  val initialViewport:Viewport
  val selectionViewports:Set[Viewport]

  val selectionFractals:Seq[Named[Fractal]]
}


