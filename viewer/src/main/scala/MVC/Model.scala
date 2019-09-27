package MVC

import java.awt.image.BufferedImage

import nutria.core.{RGBA, _}
import nutria.data.content.{FractalCalculation, StreamByResolution}
import nutria.data.image.SaveFolder
import nutria.data.{DoubleSequence, Image}
import util.Observable

@SerialVersionUID(1L)
class Model(private var thisFractal: FractalCalculation,
            private var thisViewport: Viewport,
            private var thisSequence: Option[Point => DoubleSequence],
            folder: SaveFolder) extends Observable[Model] {

  var buffer: BufferedImage = _
  var img: Image = _

  var runningRendering: Option[Thread] = None
  render()

  def render() = {
    runningRendering.foreach(_.stop())
    runningRendering = Some(new Thread(new Runnable {
      val fc = thisFractal
      def run(): Unit =
        for {
          rawImage <- StreamByResolution(thisViewport, Dimensions(16, 9), 8, fc.content)
          coloredImage = fc.postprocessing(rawImage)
        } {
          img = coloredImage
          buffer = Image.buffer(img, RGBA.white)
          notifyObservers()
        }
    })
    )
    runningRendering.foreach(_.start())
  }

  def save() = {
    Image.verboseSave(img, RGBA.white, folder /~ s"$thisViewport.png")
  }

  def fractal_=(_fractal: FractalCalculation): Unit = {
    thisFractal = _fractal
    render()
  }

  def fractal = thisFractal

  def viewport_=(v: Viewport) {
    thisViewport = v
    render()
  }

  def viewport = thisViewport

  def sequence_=(s: Option[Point => DoubleSequence]): Unit = {
    thisSequence = s
    render()
  }

  def sequence = thisSequence
}
