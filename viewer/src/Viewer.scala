import MVC.model._
import MVC.view._
import nutria.fractal.{JuliaSet, Mandelbrot}
import nutria.fractal.techniques.{EscapeTechniques, TrapTechniques}

object Viewer {
  def main(args: Array[String]): Unit = {


    val model = new Model()
//    model.setFractal(TrapTechniques[Mandelbrot.Sequence].SmoothColoring(500))

        val julia = JuliaSet(-0.6, 0.6)
        import julia.seqConstructor
        model.setSequence(Some(julia.seqConstructor))
    model.setFractal(TrapTechniques[julia.Sequence].SmoothColoring(500))
    new View(model)
  }
}


