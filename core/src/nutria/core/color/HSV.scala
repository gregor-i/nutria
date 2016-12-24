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

package nutria.core.color

import nutria.core.Color

trait HSV[A] extends Color[A] {
  def H(lambda: A): Double
  def S(lambda: A): Double
  def V(lambda: A): Double

  def apply(lambda: A): RGB = {
//    require(0 <= lambda && lambda <= 1)
    HSV2RGB(H(lambda), S(lambda), V(lambda))
  }

  def HSV2RGB(H: Double, S: Double, V: Double): RGB = {
    require(0 <= H && H <= 360)
    require(0 <= S && S <= 1)
    require(0 <= V && V <= 1)

    val h = (H / 60).toInt
    val f = H / 60.0 - h
    val q = V * (1 - S * f)
    val p = V * (1 - S)
    val t = V * (1 - S * (1 - f))

    h % 6 match {
      case 0 => RGB(V*255, t*255, p*255)
      case 1 => RGB(q*255, V*255, p*255)
      case 2 => RGB(p*255, V*255, t*255)
      case 3 => RGB(p*255, q*255, V*255)
      case 4 => RGB(t*255, p*255, V*255)
      case 5 => RGB(V*255, p*255, q*255)
    }
  }
}

object HSV {
  case object TestColor extends HSV[Double] {
    def H(lambda: Double) = lambda * 360 + 60
    def S(lambda: Double) = if (lambda < 0.5) 1 else 2 * (1 - lambda)
    def V(lambda: Double) = if (lambda < 0.5) 2 * lambda else 1
  }

  case object Rainbow extends HSV[Double] {
    def H(lambda: Double) = lambda * 360 + 240
    def S(lambda: Double) = 1
    def V(lambda: Double) = 1
  }

  object MonoColor {
    abstract class HSVMonoColor(H: Double) extends HSV[Double] {
      def H(lambd: Double) = H
      def S(lambda: Double) = if (lambda < 0.5) 1 else 2 * (1 - lambda)
      def V(lambda: Double) = if (lambda < 0.5) 2 * lambda else 1
    }

    case object Red extends HSVMonoColor(0)
    case object Yellow extends HSVMonoColor(60)
    case object Green extends HSVMonoColor(120)
    case object Cyan extends HSVMonoColor(180)
    case object Blue extends HSVMonoColor(240)
    case object Magenta extends HSVMonoColor(300)
  }

  object MonoColor2 {
    abstract class HSVMonoColor(H: Double) extends HSV[Double] {
      def H(lambd: Double) = H
      def S(lambda: Double) = 1 - math.sqrt(1 - math.pow(1 - lambda, 2))
      def V(lambda: Double) = math.sqrt(1 - math.pow(1 - lambda, 2))
    }

    case object Red extends HSVMonoColor(0)
    case object Yello extends HSVMonoColor(60)
    case object Green extends HSVMonoColor(120)
    case object Cyan extends HSVMonoColor(180)
    case object Blue extends HSVMonoColor(240)
    case object Magenta extends HSVMonoColor(300)
  }
}


