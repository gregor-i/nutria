package io

import cats.data.Xor
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import nutria.viewport.Viewport

object ViewportExporter {
  def apply(data: Set[Viewport]): String = data.asJson.spaces2
}

object ViewportImporter {
  def apply(data: String): Xor[Error, Set[Viewport]] =
    decode[Set[Viewport]](data)
}