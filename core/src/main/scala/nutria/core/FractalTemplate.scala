package nutria.core

import nutria.CirceCodec

@monocle.macros.Lenses()
case class FractalTemplate(code: String, parameters: Vector[Parameter])

object FractalTemplate extends CirceCodec {
  implicit val codec = semiauto.deriveConfiguredCodec[FractalTemplate]

  implicit val ordering: Ordering[FractalTemplate] = Ordering.by[FractalTemplate, String](_.code)

  def applyParameters(template: FractalTemplate)(parameters: Vector[Parameter]) =
    FractalTemplate(template.code, Parameter.setParameters(template.parameters, parameters))
}
