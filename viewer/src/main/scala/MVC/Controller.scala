package MVC

import java.awt.event._

import MVC.view.View
import nutria.data.fractalFamilies.MandelbrotFamily

class Controller(val view: View) extends KeyListener with MouseListener with MouseWheelListener with MouseMotionListener {
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
    val px = e.getX / imgPanel.getWidth.toDouble
    val py = e.getY / imgPanel.getHeight.toDouble
    view.model.viewport = view.model.viewport.focus(px, py)
  }

  class DocAction(descr: String, run: => Unit) {
    def unsafePerformIO(): Unit = run
    override def toString: String = descr
  }
  val keyCodePretty: Map[Int, String] = Map(
    KeyEvent.VK_DOWN -> "Down",
    KeyEvent.VK_UP -> "Up",
    KeyEvent.VK_RIGHT -> "Right",
    KeyEvent.VK_LEFT -> "Left",
    KeyEvent.VK_ADD -> "+",
    KeyEvent.VK_SUBTRACT -> "-",
    KeyEvent.VK_ENTER -> "Enter",
    KeyEvent.VK_R -> "R",
    KeyEvent.VK_H -> "H"
  ).withDefaultValue("Undefined")

  val keyMap: Map[Int, DocAction] = Map(
    KeyEvent.VK_DOWN -> new DocAction("Move viewport down", view.model.viewport = view.model.viewport.down()),
    KeyEvent.VK_UP -> new DocAction("Move viewport up", view.model.viewport = view.model.viewport.up()),
    KeyEvent.VK_RIGHT -> new DocAction("Move viewport right", view.model.viewport = view.model.viewport.right()),
    KeyEvent.VK_LEFT -> new DocAction("Move viewport left", view.model.viewport = view.model.viewport.left()),
    KeyEvent.VK_ADD -> new DocAction("Zoom in", view.model.viewport = view.model.viewport.zoomIn()),
    KeyEvent.VK_SUBTRACT -> new DocAction("Zoom out", view.model.viewport = view.model.viewport.zoomOut()),
    KeyEvent.VK_ENTER -> new DocAction("Print current position", println(view.model.viewport.toString)),
    KeyEvent.VK_R -> new DocAction("Reset viewport", view.model.viewport = MandelbrotFamily.initialViewport),
    KeyEvent.VK_H -> new DocAction("Display this help", println(keyMap map { case (keyCode, docu) => s"[${keyCodePretty(keyCode)}]\t $docu" } mkString "\n"))
  ).withDefaultValue(new DocAction("Undefined", ()))

  override def keyPressed(arg0: KeyEvent) = keyMap(arg0.getKeyCode).unsafePerformIO()

  override def mouseWheelMoved(e: MouseWheelEvent) = {
    val px = e.getX / imgPanel.getWidth.toDouble
    val py = e.getY / imgPanel.getHeight.toDouble
    view.model.viewport = view.model.viewport.zoomSteps((px, py), -e.getWheelRotation)
  }

  override def mouseMoved(e: MouseEvent): Unit = imgPanel.repaint()
  override def mouseDragged(e: MouseEvent): Unit = ()
}
