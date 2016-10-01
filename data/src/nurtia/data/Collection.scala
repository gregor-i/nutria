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

package nurtia.data

import nutria.SequenceConstructor
import nutria.sequences.{Collatz, DoubleSequence, Mandelbrot}

object Collection {

  val factories = Seq(
    SimpleFactory, AntiAliaseFactory, BuddhaBrotFactory
  )

  val fractals: Seq[(String, SequenceConstructor[_ <: DoubleSequence], Data[_])] =
    Seq(
      ("Mandelbrot", Mandelbrot(50), MandelbrotData),
      ("Collatz", Collatz(50), CollatzData)
      //      ("MandelbrotCube", MandelbrotCube, MandelbrotCube.fractals),
      //      ("Burning Ship", BurningShip, BurningShip.fractals),
      //      ("JuliaSet(-0.6, -0.6)", JuliaSet(-0.6, -0.6), JuliaSet(-0.6, -0.6).fractals),
      //      ("JuliaSet(-0.4, 0.6)", JuliaSet(-0.4, 0.6), JuliaSet(-0.4, 0.6).fractals),
      //      ("JuliaSet-0.8, 0.156)", JuliaSet(-0.8, 0.156), JuliaSet(-0.8, 0.156).fractals),
      //      ("Tricorn", Tricorn, Tricorn.fractals)
    )
}
