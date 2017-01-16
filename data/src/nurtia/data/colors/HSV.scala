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

package nurtia.data.colors

import nutria.core.colors.HSV


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