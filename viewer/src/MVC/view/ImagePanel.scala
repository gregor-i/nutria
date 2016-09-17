package MVC.view

import javax.swing.JPanel
import java.awt.image.BufferedImage
import java.awt.Graphics
import util._
import MVC.model.Model

import entities.viewport.Transform
import entities.viewport.Dimensions
import entities.viewport._
import entities.syntax._

class ImagePanel(val modell: Model) extends JPanel with Observer {
  modell.addObserver(this)
  setFocusable(true)
  setPreferredSize(new java.awt.Dimension(1000, 800))

  def invert(p: Point) = {
    val trans = modell.view.withDimensions(Dimensions(getWidth, getHeight))
    new Point(trans.invertX(p.x, p.y), trans.invertY(p.x, p.y))
  }

  def line(g: Graphics, a: Point, b: Point) {
    val inva = invert(a)
    val invb = invert(b)
    g.drawLine(inva.x.toInt, inva.y.toInt, invb.x.toInt, invb.y.toInt)
  }

  override def paint(g: Graphics) {
    //g.clearRect(0, 0, getWidth(), getHeight())
    g.drawImage(modell.img, 0, 0, getWidth, getHeight, this)

    g.setColor(java.awt.Color.red)
    val trans = modell.view.withDimensions(Dimensions(getWidth, getHeight))

    // link points
//    val factorBeyond = 10
//    for {
//      iterator <- modell.points.sliding(2)
//      p1 = iterator.head
//      p2 = iterator.tail.head
//      invertP1 = trans.invert(p1.x, p1.y)
//      invertP2 = trans.invert(p2.x, p2.y)
//      if invertP1._1 >= -(factorBeyond-1)*getWidth && invertP1._1 < factorBeyond*getWidth
//      if invertP2._1 >= -(factorBeyond-1)*getWidth && invertP2._1 < factorBeyond*getWidth
//      if invertP1._2 >= -(factorBeyond-1)*getHeight && invertP1._2 < factorBeyond*getHeight
//      if invertP2._2 >= -(factorBeyond-1)*getHeight && invertP2._2 < factorBeyond*getHeight
//    }{
//        g.drawLine(invertP1._1.toInt, invertP1._2.toInt, invertP2._1.toInt, invertP2._2.toInt)
//    }


    // One Cross per Point
    for (p <- modell.points) {
      val posx = trans.invertX(p.x, p.y).toInt
      val posy = trans.invertY(p.x, p.y).toInt
      g.drawLine(posx - 25, posy - 25, posx + 25, posy + 25)
      g.drawLine(posx - 25, posy + 25, posx + 25, posy - 25)
    }

    /*
        // Kreuze fuer alle Fokus-Viewports
    for (f <- Fokus.iteration1) {
      val p = (f.a+f.b)*0.5
      val posx = trans.invertX(p.x, p.y).toInt
      val posy = trans.invertY(p.x, p.y).toInt
      g.drawLine(posx - 25, posy - 25, posx + 25, posy + 25)
      g.drawLine(posx - 25, posy + 25, posx + 25, posy - 25)
    }
    */

    /* if (modell.points.size >= 2) {
       val a = modell.points(0)
       val b = modell.points(1)

       line(g, a, b)

       val diff = b - a
       val norm = diff norm
       val rot = Fokus.rotAngle(diff)
       val rotOrth = rot.orth()

       val A = rot * ((rot * diff) / (norm*norm))
   val B = rotOrth * ((rotOrth * diff) / (norm*norm))

 //  val A = rot
 //      val B = rotOrth


       g.setColor(java.awt.Color.green)
       line(g, a, a+A)
       line(g, a, a+B)
       line(g, a+A, a+B+A)
       line(g, a+B, a+B+A)


       val TA = A * ( 1.0 / Fokus.Fdelta.y)
       val TB = B * ( 1.0 / Fokus.Fdelta.x)
       val U = a - TA * Fokus.FA.y - TB*Fokus.FA.x


       g.setColor(java.awt.Color.blue)
       line(g, U, U+TA)
       line(g, U, U+TB)
       line(g, U+TA, U+TB+TA)
       line(g, U+TB, U+TB+TA)

     }*/
  }

  override def update(caller: Observable) = repaint()
}
