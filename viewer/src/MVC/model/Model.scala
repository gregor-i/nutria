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

package MVC.model

import nurtia.data.{ContentFactory, MandelbrotData, SimpleFactory}
import nutria.core.color.HSV
import nutria.core.consumers.RoughColoring
import nutria.core.sequences.{DoubleSequence, Mandelbrot}
import nutria.core.syntax._
import nutria.core.viewport.{Dimensions, Point}
import nutria.core._
import nutria.core.image.SaveFolder
import util.Observable

object Model {
  def default(implicit folder:SaveFolder) = new Model(
    Mandelbrot(250, 100) ~> RoughColoring(),
    SimpleFactory,
    HSV.MonoColor.Blue,
    MandelbrotData.initialViewport,
    Some(Mandelbrot(50, 100)))
}

@SerialVersionUID(1L)
class Model(var fractal: Fractal,
            var contentFactory: ContentFactory,
            var farbe: Color[Double],
            var view: Viewport,
            var sequenceConstructor: Option[SequenceConstructor[_ <: DoubleSequence]])
           (implicit folder:SaveFolder)extends Observable {

  var quali: Double = 0.25
  var img: Image[_] = _
  var points = Seq[Point]()

  preview()

  def preview() = {
    img = contentFactory(view, Dimensions.fullHD.scale(quali), fractal, farbe)
    notifyObservers()
  }

  def snap() = {
    img = contentFactory(view, Dimensions.fullHD, fractal, farbe)
    notifyObservers()
  }

  def save() = {
    img.verboseSave(folder /~ s"$view.png")
  }

  def setViewport(v: Viewport) {
    require(v != null)
    view = v
    preview()
  }

  def setColor(f: Color[Double]) {
    require(f != null)
    farbe = f
    preview()
  }

  def setImageFactory(b: ContentFactory) = {
    require(b != null)
    contentFactory = b
    preview()
  }

  def setFractal(f: Fractal) = {
    require(f != null)
    fractal = f
    preview()
  }

  def setQuali(q: Double) = {
    require(q >= 0 && q <= 1)
    quali = q
    preview()
  }

  def setSequence(newSequenceConstructor: Option[SequenceConstructor[_ <: DoubleSequence]]): Unit = {
    sequenceConstructor = newSequenceConstructor
    notifyObservers()
  }

  def setPoints(ps: Seq[Point]) = {
    points = ps
    notifyObservers()
  }
}