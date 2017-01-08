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

import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame

class ImagePanel(val name:String, val image: BufferedImage) extends JFrame(name) {
  setPreferredSize(new java.awt.Dimension(1000, 800))
  pack()
  setVisible(true)

  override def paint(g: Graphics) {
    g.drawImage(image, 0, 0, getWidth, getHeight, null)
  }

}