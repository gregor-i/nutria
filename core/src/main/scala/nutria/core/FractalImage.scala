package nutria.core

import io.circe.Codec
import monocle.Lens
import nutria.{CirceCodec, core}

@monocle.macros.Lenses()
case class FractalImage(
    template: FractalTemplate,
    viewport: core.Viewport,
    antiAliase: AntiAliase = 1,
    parameters: Vector[Parameter] = Vector.empty
) {
  def appliedParameters                               = Parameter.setParameters(template.parameters, parameters)
  def setParameters(newParameters: Vector[Parameter]) = copy(parameters = Parameter.setParameters(parameters, newParameters))
}

object FractalImage extends CirceCodec {
  def fromTemplate(template: FractalTemplate): FractalImage =
    FractalImage(template = template, viewport = template.exampleViewport)

  val appliedParameters: Lens[FractalImage, Vector[Parameter]] =
    Lens[FractalImage, Vector[Parameter]](get = _.appliedParameters)(set = newParameters => _.setParameters(newParameters))

  implicit val codec: Codec[FractalImage] = semiauto.deriveConfiguredCodec

  implicit val ordering: Ordering[FractalImage] = Ordering.by(image => (image.template, image.viewport))
}
