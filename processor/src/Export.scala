import io.circe.Encoder
import io.circe.generic.auto._

import nutria.viewport.Viewport

object Export {

  val encoder = implicitly[Encoder[Viewport]]
  def main(args: Array[String]): Unit = {

    println(encoder.encode(viewportSelections.ViewportSelection)
  }
}
