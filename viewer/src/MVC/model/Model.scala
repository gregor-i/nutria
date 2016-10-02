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

import java.awt.image.BufferedImage

import nurtia.data.{ContentFactory, SimpleFactory}
import nutria.color.{HSV}
import nutria.consumers.RoughColoring
import nutria.sequences.{DoubleSequence, Mandelbrot}
import nutria.syntax._
import nutria.viewport.{Dimensions, Point, Viewport}
import nutria.{Color, Fractal, SequenceConstructor}
import util.Observable

@SerialVersionUID(1L)
class Model(
             var fractal: Fractal = Mandelbrot(250) ~> RoughColoring(),
             var contentFactory: ContentFactory = SimpleFactory,
             var farbe: Color = HSV.MonoColor.Blue,
             var view: Viewport = Mandelbrot.start) extends Observable {

  var quali: Double = 0.25
  var img: BufferedImage = _
  var points = Seq[Point]()
  var sequenceConstructor: Option[SequenceConstructor[_ <: DoubleSequence]] = Some(Mandelbrot.apply(50))

  preview()

  def preview() {
    img = contentFactory(view, Dimensions.fullHD.scale(quali), fractal, farbe).buffer
    notifyObservers()
  }

  def snap() {
    img = contentFactory(view, Dimensions.fullHD, fractal, farbe).buffer
    notifyObservers()
  }

  def save() {
    val image = contentFactory(view, Dimensions.fullHD, fractal, farbe)
    image.verboseSave(s"snapshots/$view")
    img = image.buffer
  }

  def setViewport(v: Viewport) {
    require(v != null)
    view = v
    preview()
  }

  def setColor(f: Color) {
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


  def setSequence(newSequenceConstructor: Option[SequenceConstructor[_ <: DoubleSequence]]): Unit ={
    sequenceConstructor = newSequenceConstructor
    notifyObservers()
  }

  def setPoints(ps:Seq[Point]) = {
    points = ps
    notifyObservers()
  }
}

