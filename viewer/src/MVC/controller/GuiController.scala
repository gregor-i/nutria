package MVC.controller

import java.awt.event._

import MVC.model.Model
import MVC.view.View
import entities.fractal.Mandelbrot
import entities.viewport.{Dimensions, Point}

import entities.syntax._

class GuiController(val modell: Model, val view: View) extends KeyListener with MouseListener with MouseWheelListener with MouseMotionListener {
  val master = view.imgPanel
  master.addMouseListener(this)
  master.addMouseWheelListener(this)
  master.addMouseMotionListener(this)
  master.addKeyListener(this)

  override def keyReleased(arg0: KeyEvent) = ()
  override def keyTyped(arg0: KeyEvent) = ()
  override def mouseClicked(e: MouseEvent) = ()
  override def mouseEntered(e: MouseEvent) = ()
  override def mouseExited(e: MouseEvent) = ()
  override def mouseReleased(e: MouseEvent) = ()

  override def mousePressed(e: MouseEvent) = {
    val trans = modell.view.withDimensions(Dimensions(master.getWidth, master.getHeight))
    val x = trans.transformX(e.getX, e.getY)
    val y = trans.transformY(e.getX, e.getY)
    println(x, y)
    modell.setPoints{
      val card = Mandelbrot.CardioidNumeric(0, 100)
      val ts = card.golden(x, y)
      Seq(ts).map(card.contour).map(Point.tupled)
    }
  }

  override def keyPressed(arg0: KeyEvent) =
    arg0.getKeyCode match {
      case KeyEvent.VK_DOWN      => modell.setViewport(modell.view.down())
      case KeyEvent.VK_UP        => modell.setViewport(modell.view.up())
      case KeyEvent.VK_RIGHT     => modell.setViewport(modell.view.right())
      case KeyEvent.VK_LEFT      => modell.setViewport(modell.view.left())
      case KeyEvent.VK_ADD       => modell.setViewport(modell.view.zoomIn())
      case KeyEvent.VK_SUBTRACT  => modell.setViewport(modell.view.zoomOut())
      case KeyEvent.VK_ENTER     => println(modell.view.toString)
      case KeyEvent.VK_S         => modell.snap()
      case KeyEvent.VK_R         => modell.setViewport(entities.viewport.ViewportUtil.start)
      case _ =>
    }

  override def mouseWheelMoved(e: MouseWheelEvent) = {
    val px = e.getX / master.getWidth.toDouble
    val py = e.getY / master.getHeight.toDouble
    modell.setViewport(modell.view.zoom(px, py, e.getWheelRotation))
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    val trans = modell.view.withDimensions(Dimensions(master.getWidth, master.getHeight))
    val x = trans.transformX(e.getX, e.getY)
    val y = trans.transformY(e.getX, e.getY)

//    modell.setPoints(new Mandelbrot.Iterator(x, y, 500).wrapped.map(Point.tupled).toSeq)
    modell.setPoints{
      val card = Mandelbrot.CardioidNumeric(0, 100)
      val g = card.golden(x, y.abs)
      val n = card.newton(g, x, y.abs)
      println(g, n)
      Seq(n, g).map(card.contour).map(Point.tupled)
    }
  }
  override def mouseDragged(e: MouseEvent): Unit = ()
}
