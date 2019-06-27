package nutria.data

import nutria.data.fractalFamilies.{NewtonFamily, _}
import nutria.data.sequences._

object Collection {
  val families: Seq[Family] =
    Seq(
      MandelbrotFamily,
      new Family("MandelbrotCube", MandelbrotCube(50)),
      new Family("Tricorn", Tricorn(50)),
      new Family("Collatz", Collatz(50)),
      new Family("BurningShip", BurningShip(50)),
      new JuliaSetFamily(new sequences.JuliaSet(-0.6, -0.6)),
      new JuliaSetFamily(new sequences.JuliaSet(-0.4, 0.6)),
      new JuliaSetFamily(new sequences.JuliaSet(-0.8, 0.156)),
      new NewtonFamily("ThreeRoots", NewtonFractalByString("x*x*x + 1", "lambda")),
      new NewtonFamily("MandelbrotNewton", NewtonFractalByString("(x*x + lambda - 1) * x - lambda", "0"))
    )
}
