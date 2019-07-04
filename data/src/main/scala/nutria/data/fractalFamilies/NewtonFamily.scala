package nutria.data.fractalFamilies

import nutria.data.Defaults
import nutria.data.consumers._
import nutria.data.content.{FractalCalculation, LinearNormalized}
import nutria.data.sequences.Newton

import scala.util.control.NonFatal

class NewtonFamily(name: String,
                   val newton: Newton) extends Family(name, newton(50)) {

  def wrappInTry[A, B](f: A => B, default: B): (A => B) = a => {
    try {
      f(a)
    } catch {
      case NonFatal(_) => default
    }
  }

  override def exampleCalculations: Seq[(String, FractalCalculation)] = Seq(
    ("RoughColoring"            , FractalCalculation(newton(50) andThen wrappInTry(CountIterations.double(), Double.MaxValue) andThen LinearNormalized(0, 50) andThen Defaults.defaultColor)),
    ("SmoothColoring"           , FractalCalculation(newton(50) andThen wrappInTry(CountIterations.smoothed(), Double.MaxValue) andThen LinearNormalized(0, 50) andThen Defaults.defaultColor)),
    ("GaussianInteger"          , FractalCalculation(newton(50) andThen wrappInTry(Trap.GaussianIntegerTraps(), Double.MaxValue) andThen LinearNormalized(0, 50) andThen Defaults.defaultColor)),
    ("NewtonColoring"           , FractalCalculation(newton(50) andThen NewtonColoring())),
    ("NewtonColoring.smooth"    , FractalCalculation(newton(50) andThen NewtonColoring.smooth(newton)))
  )
}
