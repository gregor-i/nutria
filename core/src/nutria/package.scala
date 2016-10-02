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

package object nutria {
  type Accumulator = nutria.accumulator.Accumulator

  type Dimensions = nutria.viewport.Dimensions
  val Dimensions = nutria.viewport.Dimensions

  type Viewport = nutria.viewport.Viewport
  val Viewport = nutria.viewport.Viewport
  type Transform = nutria.viewport.Transform

  type Fractal = (Double, Double) => Double

  type AbstractSequence = sequences.AbstractSequence

  type SequenceConstructor[A <: AbstractSequence] = (Double, Double) => A
  type SequenceConsumer[A <: AbstractSequence] = A => Double

  type Content = nutria.content.Content
  type FinishedContent = Content with nutria.content.Normalized

  type Color = Double => Int

  type Image = nutria.image.Image
}
