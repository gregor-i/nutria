package MVC.model

import java.awt.image.BufferedImage

import MVC.{ContentFactory, SimpleFactory}
import nutria.Fractal
import nutria.color.{Color, HSV}
import nutria.fractal.techniques.EscapeTechniques
import nutria.fractal.{DoubleSequence, Mandelbrot, SequenceConstructor}
import nutria.syntax._
import nutria.viewport.{Point, Viewport}
import util.Observable


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
    img = contentFactory.simple(this).linearNormalized.withColor(farbe).buffer
    notifyObservers()
  }

  def snap() {
    img = contentFactory.beaty(this).linearNormalized.withColor(farbe).buffer
    notifyObservers()
  }

  def save() {
    val image = contentFactory.beaty(this).linearNormalized.withColor(farbe)
    image.save(s"snapshots\\$view")
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

