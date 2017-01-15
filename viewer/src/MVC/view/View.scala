/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package MVC.view

import java.awt.Frame
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JFrame, JMenu, JMenuBar, JMenuItem}

import MVC.controller.GuiController
import MVC.model.Model
import nurtia.data.{Collection, DirectFactory}

case class View(model: Model) extends JFrame {
  val imgPanel = new ImagePanel(model)
  add(imgPanel)

  val controller = new GuiController(model, this)

  val myMenuBar = new JMenuBar()


  {
    // Menu for the selection of the ContentFactory
    val menu = new ContentFactoryMenu(model)
    for (factory <- Collection.factories) {
      menu.add(
        new MenuItem(
          factory.toString,
          () => model.setImageFactory(factory)))
    }
    myMenuBar.add(menu)
  }

  {
    // Menu for the selection of the fractal
    val menu = new FractalMenu(model)
    for (data <- Collection.doubleSequenceFractals) {
      val subMenu = new JMenu(data.name)
      for ((fractalName, fractal) <- data.selectionFractals)
        subMenu.add(
          new MenuItem(
            fractalName,
            () => {
              model.setFractal(fractal)
              model.setSequence(Some(data.exampleSequenceConstructor))
            }))
      menu.add(subMenu)
      for ((fractalName, fractal) <- data.directFractals)
        subMenu.add(
          new MenuItem(
            fractalName,
            () => {
              model.setImageFactory(DirectFactory(fractal))
              model.setSequence(Some(data.exampleSequenceConstructor))
            }))
      menu.add(subMenu)
    }
    for (data <- Collection.abstractFracals) {
      val subMenu = new JMenu(data.name)
      for ((fractalName, fractal) <- data.selectionFractals)
        subMenu.add(
          new MenuItem(
            fractalName,
            () => {
              model.setFractal(fractal)
              model.setSequence(None)
            }))
      menu.add(subMenu)
    }
    myMenuBar.add(menu)
  }

//  {
//    // Auswahl Menu
//    val menu = new JMenu("Auswahl")
//    var i = 0
//    var sub: JMenu = null
//    for (viewport <- nutria.viewport.Viewport.auswahl) {
//      if (i % 10 == 0) {
//        sub = new JMenu("sub %d".format(i / 10))
//        menu.add(sub.asInstanceOf[JMenuItem])
//      }
//      sub.add(
//        new MenuItem(
//          "%d".format(i),
//          () => model.setViewport(viewport)))
//      i = i + 1
//    }
//    myMenuBar.add(menu)
//  }

//  {
//    // Auswahl Menu
//    val menu = new JMenu("FokusAuswahl1")
//    var i = 0
//    var sub: JMenu = null
//    for (viewport <- nutria.viewport.Fokus.iteration1) {
//      if (i % 10 == 0) {
//        sub = new JMenu("sub %d".format(i / 10))
//        menu.add(sub.asInstanceOf[JMenuItem])
//      }
//      sub.add(
//        new MenuItem(
//          "%d".format(i),
//          () => model.setViewport(viewport)))
//      i = i + 1
//    }
//    myMenuBar.add(menu)
//  }

  {
    // Save Menu
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

  //  { // fokus von points
  //    val menuItem = new JMenuItem("Fokus")
  //    menuItem.addActionListener(new ActionListener() {
  //      override def actionPerformed(arg0: ActionEvent) {
  //        if (model.points.length >= 2) {
  //          val a = model.points(0)
  //          val b = model.points(1)
  //          val fokus = new entities.viewport.Fokus(a, b)
  //          model.setViewport(fokus)
  //          model.clearPoints()
  //        }
  //      }
  //    })
  //    myMenuBar.add(menuItem)
  //  }


  setJMenuBar(myMenuBar)
  //  myMenuBar.add(new ColorMenu(DefaultColors.instances, modell))
  setFocusable(false)
  pack()
  setExtendedState(Frame.MAXIMIZED_BOTH)
  setVisible(true)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
}
