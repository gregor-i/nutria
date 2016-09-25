package MVC.model

import java.awt.image.BufferedImage

import nurtia.data.{ContentFactory, SimpleFactory}
import nutria.Fractal
import nutria.color.{Color, HSV}
import nutria.fractal.techniques.EscapeTechniques
import nutria.fractal.{DoubleSequence, Mandelbrot, SequenceConstructor}
import nutria.viewport.{Dimensions, Point, Viewport}
import util.Observable

import nutria.syntax._

@SerialVersionUID(1L)
class Model(
             var fractal: Fractal = EscapeTechniques[Mandelbrot.Sequence].RoughColoring(250),
             var contentFactory: ContentFactory = SimpleFactory,
             var farbe: Color = HSV.MonoColor.Blue,
             var view: Viewport = Mandelbrot.start) extends Observable {

  var quali: Double = 0.25
  var img: BufferedImage = _
  var points = Seq[Point]()
  var sequenceConstructor: Option[SequenceConstructor[_ <: DoubleSequence]] = Some(Mandelbrot.seqConstructor)

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

