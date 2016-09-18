package MVC

import MVC.model.Model
import nutria.content.{AntiAliasedFractalContent, BuddahBrot}
import nutria.viewport.Dimensions
import nutria.{Content, Transform}

import nutria.syntax._

trait ContentFactory {
  def simple(model: Model): Content

  def beaty(model: Model): Content
}

case object SimpleFactory extends ContentFactory {
  def simple(model: Model): Content =
    model.view.withDimensions(Dimensions.fullHD.scale(model.quali)).withFractal(model.fractal)

  def beaty(model: Model): Content =
    model.view.withDimensions(Dimensions.fullHD).withFractal(model.fractal)
}

case object AntiAliaseFactory extends ContentFactory {
  def simple(model: Model): Content =
    model.view.withDimensions(Dimensions.fullHD.scale(model.quali)).withAntiAliasedFractal(model.fractal)

  def beaty(model: Model): Content =
    model.view.withDimensions(Dimensions.fullHD).withAntiAliasedFractal(model.fractal)
}

case object BuddhaBrotFactory extends ContentFactory {
  def simple(model: Model): Content =
    model.view.withDimensions(Dimensions.fullHD.scale(model.quali)).withBuddhaBrot()

  def beaty(model: Model): Content =
    model.view.withDimensions(Dimensions.fullHD).withBuddhaBrot()
}
