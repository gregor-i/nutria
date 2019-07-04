package nutria.data.fractalFamilies

import nutria.core.{Point, Viewport}
import nutria.data.consumers.{CountIterations, Trap}
import nutria.data.content.{FractalCalculation, LinearNormalized}
import nutria.data.{Defaults, DoubleSequence}

class Family(val name: String,
             val exampleSequenceConstructor: Point => DoubleSequence) {
  val initialViewport: Viewport = Defaults.defaultViewport
  val exampleViewports: Seq[Viewport] = Seq.empty
  def exampleCalculations: Seq[(String, FractalCalculation)] =
    Family.exampleCalculations(exampleSequenceConstructor)
}

object Family {
  def exampleCalculations(f: Point => DoubleSequence) = Seq(
    ("RoughColoring(50)"    , FractalCalculation(f andThen CountIterations.double() andThen LinearNormalized(0, 50) andThen Defaults.defaultColor)),
    ("SmoothColoring(50)"   , FractalCalculation(f andThen CountIterations.smoothed() andThen LinearNormalized(0, 50) andThen Defaults.defaultColor)),
    ("OrbitPoint(50, 0, 0)" , FractalCalculation(f andThen Trap.OrbitPoint(0, 0) andThen LinearNormalized(0, 50) andThen Defaults.defaultColor))
  )
}

