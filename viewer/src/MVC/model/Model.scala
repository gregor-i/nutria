package MVC.model

import java.awt.image.BufferedImage

import MVC.{ContentFactory, SimpleFactory}
import entities.Fractal
import entities.syntax._
import entities.color.{Color, HSV}
import entities.viewport.{Point, Viewport, ViewportUtil}
import util.Observable


@SerialVersionUID(1L)
class Model(
  var fractal: Fractal = entities.fractal.Mandelbrot.RoughColoring(250),
  var contentFactory: ContentFactory = SimpleFactory,
  var farbe: Color = HSV.MonoColor.Blue,
  var view: Viewport = ViewportUtil.start) extends Observable {

  var quali: Double = 0.25
  var img: BufferedImage = _
  var points = Seq[Point]()

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

  def setPoints(ps:Seq[Point]) = {
    points = ps
    notifyObservers()
  }

  def addPoint(p: Point) = {
    points = p +: points
    notifyObservers()
  }
  
  def clearPoints() = {
    points = List()
    notifyObservers()    
  }
}

