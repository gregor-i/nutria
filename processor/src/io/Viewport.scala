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

package io

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import nutria.core.Viewport

object ViewportExporter {
  def apply(data: Set[Viewport]): String = data.asJson.spaces2
}

object ViewportImporter {
  def apply(data: String): Either[Error, Set[Viewport]] =
    decode[Set[Viewport]](data)
}