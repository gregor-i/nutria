package MVC.controller

import java.awt.event._

import MVC.model.Model
import MVC.view.View
import nutria.color.Invert
import nutria.fractal.Mandelbrot
import nutria.viewport.{Dimensions, Point}
import nutria.syntax._

class GuiController(val modell: Model, val view: View) extends KeyListener with MouseListener with MouseWheelListener with MouseMotionListener {
  val imgPanel = view.imgPanel
  imgPanel.addMouseListener(this)
  imgPanel.addMouseWheelListener(this)
  imgPanel.addMouseMotionListener(this)
  imgPanel.addKeyListener(this)

  override def keyReleased(arg0: KeyEvent) = ()
  override def keyTyped(arg0: KeyEvent) = ()
  override def mouseClicked(e: MouseEvent) = ()
  override def mouseEntered(e: MouseEvent) = ()
  override def mouseExited(e: MouseEvent) = ()
  override def mouseReleased(e: MouseEvent) = ()

  override def mousePressed(e: MouseEvent) = {
    val trans = modell.view.withDimensions(Dimensions(imgPanel.getWidth, imgPanel.getHeight))
    val x = trans.transformX(e.getX, e.getY)
    val y = trans.transformY(e.getX, e.getY)
    println(s"Clicked in ($x, $y)")
  }

  override def keyPressed(arg0: KeyEvent) =
    arg0.getKeyCode match {
      case KeyEvent.VK_DOWN      => modell.setViewport(modell.view.down())
      case KeyEvent.VK_UP        => modell.setViewport(modell.view.up())
      case KeyEvent.VK_RIGHT     => modell.setViewport(modell.view.right())
      case KeyEvent.VK_LEFT      => modell.setViewport(modell.view.left())
      case KeyEvent.VK_ADD       => modell.setViewport(modell.view.zoomIn())
      case KeyEvent.VK_SUBTRACT  => modell.setViewport(modell.view.zoomOut())
      case KeyEvent.VK_I         => modell.setColor(Invert.invert(modell.farbe))
      case KeyEvent.VK_ENTER     => println(modell.view.toString)
      case KeyEvent.VK_S         => modell.snap()
      case KeyEvent.VK_R         => modell.setViewport(nutria.fractal.Mandelbrot.start)
      case _ =>
    }

  override def mouseWheelMoved(e: MouseWheelEvent) = {
    val px = e.getX / imgPanel.getWidth.toDouble
    val py = e.getY / imgPanel.getHeight.toDouble
    modell.setViewport(modell.view.zoom(px, py, e.getWheelRotation))
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    val trans = modell.view.withDimensions(Dimensions(imgPanel.getWidth, imgPanel.getHeight))
    val x = trans.transformX(e.getX, e.getY)
    val y = trans.transformY(e.getX, e.getY)

    modell.sequenceConstructor.map(_.apply(x, y, 50)).map(_.wrapped).map(points => Point(x, y) +: points.filter(t => t._1 == t._1 && t._2 == t._2).map(Point.tupled).toSeq).foreach(modell.setPoints)

//    match
//      case seq @ Some[Sequ] => modell.setPoints(construcor.sequence(x,y, 50).wrapped.map(Point.tupled).toSeq)
//      case None =>
//    }

//    modell.setPoints{
//      val card = Mandelbrot.CardioidNumeric(0, 100)
//      val g = card.golden(x, y.abs)
//      val n = card.newton(g, x, y.abs)
//      Seq(n, g).map(card.contour).map(Point.tupled)
//    }
  }
  override def mouseDragged(e: MouseEvent): Unit = ()
}
