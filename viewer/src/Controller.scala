import javafx.beans.binding.ObjectBinding

import nurtia.data.{ContentFactory, SimpleFactory}
import nurtia.data.colors.Wikipedia
import nurtia.data.consumers.RoughColoring
import nurtia.data.fractalFamilies.MandelbrotData
import nurtia.data.sequences.Mandelbrot
import nutria.core._
import nutria.core.viewport.Viewport
import nutria.core.syntax._

import scalafx._
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.control.ProgressBar
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml

@sfxml
class Controller(private val fractalView : ImageView, private val renderProgress : ProgressBar){
  val fractal: ObjectProperty[ContentFunction[Double]] = ObjectProperty(Mandelbrot(250, 10) ~> RoughColoring.double())
  val contentFactory: ObjectProperty[ContentFactory] = ObjectProperty(SimpleFactory)
  val view: ObjectProperty[Viewport] = ObjectProperty(MandelbrotData.initialViewport)
  val colour: ObjectProperty[Color[Double]] = ObjectProperty(Wikipedia)
  val quality: DoubleProperty = DoubleProperty(0.25)

  val dimensions: ObjectProperty[Dimensions] = ObjectProperty(new Dimensions(fractalView.fitWidth.toInt, fractalView.fitHeight.toInt))
  dimensions <== new ObjectBinding[Dimensions]{
    bind(fractalView.fitWidthProperty, fractalView.fitHeightProperty)
    override def computeValue: Dimensions = Dimensions(fractalView.fitWidth.toInt, fractalView.fitHeight.toInt)
  }

  val nutriaImage: ObjectProperty[nutria.core.Image] = ObjectProperty(contentFactory.value.apply(view.value, dimensions.value.scale(quality.value), fractal.value, colour.value))
  nutriaImage <== new ObjectBinding[nutria.core.Image] {
    bind(view, dimensions, quality, fractal, colour)
    override def computeValue(): nutria.core.Image = contentFactory.value.apply(view.value, dimensions.value.scale(quality.value), fractal.value, colour.value)
  }

  fractalView.imageProperty.bind(new ObjectBinding[javafx.scene.image.Image] {
    bind(nutriaImage)
    override def computeValue(): javafx.scene.image.Image = SwingFXUtils.toFXImage(nutriaImage.value.buffer, null)
  })
}
