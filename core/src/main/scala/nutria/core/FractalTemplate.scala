package nutria.core

import nutria.CirceCodec

@monocle.macros.Lenses()
case class FractalTemplate(code: String, parameters: Vector[Parameter], exampleViewport: Viewport)

object FractalTemplate extends CirceCodec {
  val empty = FractalTemplate("", Vector.empty, Viewport.aroundZero)

  implicit val codec = semiauto.deriveConfiguredCodec[FractalTemplate]

  implicit val ordering: Ordering[FractalTemplate] = Ordering.by[FractalTemplate, String](_.code)

  def applyParameters(template: FractalTemplate)(parameters: Vector[Parameter]) =
    FractalTemplate(template.code, Parameter.setParameters(template.parameters, parameters), template.exampleViewport)
}
