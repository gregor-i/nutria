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

import java.awt.event.{ActionEvent, ActionListener}

import mathParser.double.DoubleLanguage
import mathParser.implicits.doubleParseLiterals
import mathParser.{Evaluate, Parser, Variable}
import nutria.core.colors.HSV
import nutria.core.syntax._
import nutria.core.{Content, Dimensions}

case class Controller(model: Model, view: View) extends Object with ActionListener {

  val lang = DoubleLanguage
  def parser(vars:Set[Variable]) = Parser(lang, vars)

  def state = s"ERG: ${view.erg.getText()}, H: ${view.H.getText()}, S: ${view.S.getText()}, V: ${view.V.getText()}"

  val colorVariables = Set('l)
  val lambdaParser = parser(colorVariables)

  val contentParser = parser(model.content.keySet)

  view.addButtonListener(this)

  override def actionPerformed(e: ActionEvent): Unit = {

    lazy val color = new HSV[Double] {
      val funH = lambdaParser(view.H.getText()).getOrElse(throw new RuntimeException("parse Error H"))
      val funS = lambdaParser(view.S.getText()).getOrElse(throw new RuntimeException("parse Error S"))
      val funV = lambdaParser(view.V.getText()).getOrElse(throw new RuntimeException("parse Error V"))

      override def H(lambda: Double): Double = Evaluate(funH){
        case 'l => lambda
      }.max(0).min(360)
      override def S(lambda: Double): Double = Evaluate(funS){
        case 'l => lambda
      }.max(0).min(1)
      override def V(lambda: Double): Double = Evaluate(funV){
        case 'l => lambda
      }.max(0).min(1)
    }


    contentParser(view.erg.getText()) match {
      case Some(parsed) =>
        val content = new Content[Double] {
          override def apply(x: Int, y: Int): Double = {
            Evaluate(parsed){
              case symbol => model.content(symbol)(x, y)
            }
          }

          override val dimensions: Dimensions = model.dimensions
        }.strongNormalized

        new ImagePanel(state, content.withColor(color).buffer)

      case None => println(s"parsing error ERG: ${view.erg.getText()}")
    }

  }
}
