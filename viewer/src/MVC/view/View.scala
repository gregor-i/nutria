package MVC.view

import java.awt.Frame
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JFrame, JMenu, JMenuBar, JMenuItem}

import MVC.{AntiAliaseFactory, BuddhaBrotFactory, ContentFactory, SimpleFactory}
import MVC.controller.GuiController
import MVC.model.Model

object Collection {

  import entities.accumulator._
  val factories = Seq(
    SimpleFactory,AntiAliaseFactory,BuddhaBrotFactory
  )

  import entities.fractal._
  val fractals = Mandelbrot.fractals ++ List(
  // new Collatz(150),

    new BurningShip(100),
    new BurningShip(500),
    new BurningShip(1000),

    new Tricorn(100),
    new Tricorn(500),
    new Tricorn(1000),

    new JuliaSet(100, -0.6, -0.6),
    new JuliaSet(500, -0.6, -0.6),
    new JuliaSet(1000, -0.6, -0.6),

    new JuliaSet(100, -0.4, 0.6),
    new JuliaSet(500, -0.4, 0.6),
    new JuliaSet(1000, -0.4, 0.6),

    new JuliaSet(100, -0.8, 0.156),
    new JuliaSet(500, -0.8, 0.156),
    new JuliaSet(1000, -0.8, 0.156),
    
    new Mandelbrot3(500))

}

@SerialVersionUID(1L)
class View(val model: Model) extends JFrame {
  val imgPanel = new ImagePanel(model)
  add(imgPanel)

  val controller = new GuiController(model, this)

  val myMenuBar = new JMenuBar()


  { // Men� f�r die Auswahl des "simple" Algorithmus (Preview-Shot)
    val menu = new ContentFactoryMenu(model)
    for (factory <- Collection.factories) {
      menu.add(
        new MenuItem(
          factory.toString(),
          new SetContentFactoryAction(factory, model)))
    }
    myMenuBar.add(menu)
  }

  { // Menü für die Auswahl des Fraktals
    val menu = new FractalMenu(model)
    for (fractal <- Collection.fractals) {
      menu.add(
        new MenuItem(
          fractal.toString(),
          new SetFractalAction(fractal, model)))
    }
    myMenuBar.add(menu)
  }

  { // Auswahl Menu
    val menu = new JMenu("Auswahl")
    var i = 0
    var sub:JMenu = null
    for (viewport <- entities.viewport.Viewport.auswahl) {
      if(i%10 == 0){
        sub = new JMenu("sub %d".format(i/10))
        menu.add(sub.asInstanceOf[JMenuItem])
      }
      sub.add(
        new MenuItem(
          "%d".format(i),
          new SetViewportAction(viewport, model)))
      i = i+1
    }
    myMenuBar.add(menu)
  }

    { // Auswahl Menu
    val menu = new JMenu("FokusAuswahl1")
    var i = 0
    var sub:JMenu = null
    for (viewport <- entities.viewport.Fokus.iteration1) {
      if(i%10 == 0){
        sub = new JMenu("sub %d".format(i/10))
        menu.add(sub.asInstanceOf[JMenuItem])
      }
      sub.add(
        new MenuItem(
          "%d".format(i),
          new SetViewportAction(viewport, model)))
      i = i+1
    }
    myMenuBar.add(menu)
  }

  { // Save Menu
    val menu = new JMenu("Save")
    var menuItem: JMenuItem = null
    menuItem = new JMenuItem("Viewport")
    menuItem.addActionListener(new ActionListener() {
      override def actionPerformed(arg0: ActionEvent) {
        println(model.view)
      }
    })
    menu.add(menuItem)
    menuItem = new JMenuItem("Image")
    menuItem.addActionListener(new ActionListener() {
      override def actionPerformed(arg0: ActionEvent) {
        model.save()
      }
    })
    menu.add(menuItem)
    myMenuBar.add(menu)
  }

  { // fokus von points
    val menuItem = new JMenuItem("Fokus")
    menuItem.addActionListener(new ActionListener() {
      override def actionPerformed(arg0: ActionEvent) {
        if (model.points.length >= 2) {
          val a = model.points(0)
          val b = model.points(1)
          val fokus = new entities.viewport.Fokus(a, b)
          model.setViewport(fokus)
          model.clearPoints()
        }
      }
    })
    myMenuBar.add(menuItem)
  }

  
  setJMenuBar(myMenuBar)
  //  myMenuBar.add(new ColorMenu(DefaultColors.instances, modell))
  setFocusable(false)
  pack()
  setExtendedState(Frame.MAXIMIZED_BOTH)
  setVisible(true)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
}
