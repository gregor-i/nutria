package MVC

import MVC.model.Model
import entities.content.{AntiAliasedFractalContent, BuddahBrot}
import entities.viewport.Dimensions
import entities.{Content, Transform}

import entities.syntax._

trait ContentFactory {
  def simple(model: Model): Content

  def beaty(model: Model): Content
}

case object SimpleFactory extends ContentFactory {
  def simple(model: Model): Content =
    model.view.withDimensions(Dimensions.screenHD.scale(model.quali)).withFractal(model.fractal)

  def beaty(model: Model): Content =
    model.view.withDimensions(Dimensions.screenHD).withFractal(model.fractal)
}

case object AntiAliaseFactory extends ContentFactory {
  def simple(model: Model): Content =
    model.view.withDimensions(Dimensions.screenHD.scale(model.quali)).withAntiAliasedFractal(model.fractal)

  def beaty(model: Model): Content =
    model.view.withDimensions(Dimensions.screenHD).withAntiAliasedFractal(model.fractal)
}

case object BuddhaBrotFactory extends ContentFactory {
  def simple(model: Model): Content =
    model.view.withDimensions(Dimensions.screenHD.scale(model.quali)).withBuddhaBrot()

  def beaty(model: Model): Content =
    model.view.withDimensions(Dimensions.screenHD).withBuddhaBrot()
}
