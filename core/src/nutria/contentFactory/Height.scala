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

//package entities.imageFactory
//
//import entities.accumulator._
//import entities.viewport.Transform
//import entities.fractal.Fractal
//import AntiAliaseConstants._
//
//class Height(private val limit: Double) extends ImageFactory {
//
//  override def apply(fractal: Fractal, trans: Transform, x_i: Int, y_i: Int): Double = {
//    val `var` = new Variance()
//    val x0 = trans.transformX(x_i, y_i)
//    val y0 = trans.transformY(x_i, y_i)
//    val dx = trans.transformX(x_i + 1, y_i + 1) - x0
//    val dy = trans.transformY(x_i + 1, y_i + 1) - y0
//    var rx = 0.0
//    var ry = 0.0
//    var n = 0
//    do {
//      rx = (rx + g) % 1
//      ry = (ry + gg) % 1
//      `var`.next(fractal(x0 + rx * dx, y0 + ry * dy))
//      n += 1
//    } while (`var`.result(n) / n > limit || 5 > n);
//    n.toDouble
//  }
//
//  override def toString(): String = "Height " + limit
//}
