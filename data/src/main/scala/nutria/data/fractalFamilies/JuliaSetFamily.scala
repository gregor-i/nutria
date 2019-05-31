package nutria.data.fractalFamilies

import nutria.core.content.{FractalCalculation, LinearNormalized}
import nutria.data.Defaults
import nutria.data.consumers.CountIterations
import nutria.data.sequences._

class JuliaSetFamily(juliaSet: JuliaSet) extends Family(juliaSet.toString, juliaSet(1000)) {
  override def exampleCalculations: Seq[(String, FractalCalculation)] = Seq(
    ("SmoothColoring(1000)", FractalCalculation(juliaSet(1000) andThen CountIterations.smoothed() andThen LinearNormalized(0, 1000) andThen Defaults.defaultColor)),
  )
}
