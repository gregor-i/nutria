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

package nutria

package object core {
  type Dimensions = nutria.core.viewport.Dimensions
  val Dimensions = nutria.core.viewport.Dimensions

  type Viewport = nutria.core.viewport.Viewport
  val Viewport = nutria.core.viewport.Viewport
  type Transform = nutria.core.viewport.Transform

  type Fractal = (Double, Double) => Double

  type AbstractSequence = sequences.AbstractSequence

  type SequenceConstructor[A <: AbstractSequence] = (Double, Double) => A
  type SequenceConsumer[A <: AbstractSequence] = A => Double

  type Content = content.Content
  type FinishedContent = Content with content.Normalized

  type RGB = nutria.core.color.RGB
  type Color = Double => RGB

  type Image = nutria.core.image.Image
}
