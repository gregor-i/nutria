package nutria.frontend

import nutria.core.FractalEntity
import nutria.core.viewport.Dimensions
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Canvas

import scala.util.chaining._

object RenderThumbnailImage {
  def apply(fractalEntity: FractalEntity): String =
    dom.document.createElement("canvas").asInstanceOf[Canvas]
      .tap(_.setAttribute("width", Dimensions.thumbnailDimensions.width.toString))
      .tap(_.setAttribute("height", Dimensions.thumbnailDimensions.height.toString))
      .tap(FractalRenderer.render(_, fractalEntity, resize = false))
      .pipe(Untyped(_).toDataURL("image/png").asInstanceOf[String])
      .pipe(_.stripPrefix("data:image/png;base64,"))
}
