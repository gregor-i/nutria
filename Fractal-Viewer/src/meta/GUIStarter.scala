package meta

import MVC.model._
import MVC.view._
import entities.color.Invert
import entities.fractal.{Fractal, Mandelbrot}
import entities.viewport.Point

object GUIStarter extends App {
//  val cardoid = Mandelbrot.Cardioid(350, 50)
//  val model  = new Model( )
//  model.setPoints(cardoid.points.map(Point.tupled))
//  model.setFractal(cardoid)
//  model.setColor(new Invert(model.farbe))
//  new View(model)



//  val p2 = Mandelbrot.CircleP2(350)
//
//
//  val p3 = Mandelbrot.OrbitPoint(350, -0.12075471698113205,-0.7437229437229438)
//  val p3_2 = Mandelbrot.OrbitPoint(350, -0.12075471698113205,0.7437229437229438)
//
//  val f = new Fractal(){
//    override def apply(x: Double, y: Double): Double = p3(x, y) min p3_2(x, y)
//  }


  //  val p2 = Mandelbrot.CircleTrap(350, -1, 0, 0.25)
  val model  = new Model()
//  model.setPoints(p2.points(100).map(Point.tupled))
  model.setFractal(Mandelbrot.CardioidNumeric(50, 10))
  model.setColor(new Invert(model.farbe))
  new View(model)

//  println("expect 0", p2.dist(-1.25, 0))
//  println("expect 0", p2.dist(-0.75, 0))
//  println("expect 0.25", p2.dist(-1, 0))
}


