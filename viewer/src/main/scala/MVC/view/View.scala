package MVC.view

import java.awt.{Color, Frame, Graphics, MouseInfo}

import MVC.{Controller, Model}
import javax.swing._
import javax.swing.event.{MenuEvent, MenuListener}
import nutria.data.content.FractalCalculation
import nutria.data.Collection
import nutria.data.consumers.NewtonColoring
import nutria.data.syntax._
import nutria.core.viewport.Dimensions
import nutria.data.sequences.NewtonFractalByString

class View(val model: Model) extends JFrame {
  val imgPanel = new JPanel {
    override def paint(g: Graphics) = {
      g.drawImage(model.buffer, 0, 0, getWidth, getHeight, this)

      model.sequence.foreach{ sequence =>
        val mouseInfo = getMousePosition()
        if(mouseInfo != null){
          val transform = model.viewport.withDimensions(Dimensions(getWidth, getHeight))
          val transformed = transform.transform(mouseInfo.x, mouseInfo.y)
          val path = sequence(transformed).map(transform.invert)
          g.setColor(Color.red)
          path.sliding(2)
            .filter(_.length == 2).foreach{
            case Seq((x1, y1), (x2, y2)) if (x2-x1).abs > 2*getWidth || (y2-y1).abs > 2*getHeight => ()
            case Seq((x1, y1), (x2, y2))  =>
              g.drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
          }
        }
      }
    }
  }


  model.addObserver(_ => this.repaint())
  imgPanel.setFocusable(true)
  imgPanel.setPreferredSize(new java.awt.Dimension(1000, 800))

  add(imgPanel)

  val controller = new Controller(this)

  val menubarFractals = {
    // Menu for the selection of the fractal
    val menu = new FractalMenu(model)
    for (data <- Collection.families) {
      val subMenu = new JMenu(data.name)
      for ((fractalName, fractal) <- data.exampleCalculations)
        subMenu.add(
          new MenuItem(
            fractalName,
            () => {
              model.fractal = fractal
              model.sequence = Some(data.exampleSequenceConstructor)
            }
          ))
      menu.add(subMenu)
    }
    menu
  }

  val menubarNewtonFractalByString = {
    val menu = new JMenu("Newton Fractal by formula")
    menu.addMenuListener(
      new MenuListener {
        var lastInput = "x^3 + 1"

        override def menuSelected(e: MenuEvent): Unit = {
          val formula = JOptionPane.showInputDialog("Formula:", lastInput)
          val fractal = NewtonFractalByString(formula, "lambda")
          lastInput = formula
          model.fractal = FractalCalculation(fractal(50) andThen NewtonColoring.smooth(fractal))
          model.sequence = Some(fractal(50))
        }

        def menuDeselected(e: MenuEvent) = ()

        def menuCanceled(e: MenuEvent) = ()
      }
    )
    menu
  }


  def menubarSave = {
    val menu = new JMenu("Save")
    val printViewport = new JMenuItem("Viewport")
    printViewport.addActionListener(_ => println(model.viewport))
    menu.add(printViewport)
    val saveImage = new JMenuItem("Image")
    saveImage.addActionListener(_ => model.save())
    menu.add(saveImage)
    menu
  }

  setJMenuBar {
    val menubar = new JMenuBar()
    menubar.add(menubarFractals)
    menubar.add(menubarNewtonFractalByString)
    menubar.add(menubarSave)
    menubar
  }
  setFocusable(false)
  pack()
  setExtendedState(Frame.MAXIMIZED_BOTH)
  setVisible(true)
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
}
