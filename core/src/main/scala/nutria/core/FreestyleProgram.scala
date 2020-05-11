package nutria.core

import nutria.CirceCodec

@monocle.macros.Lenses()
case class FreestyleProgram(code: String, parameters: Vector[Parameter] = Vector.empty) {
  def setParameter(newParameter: Parameter): FreestyleProgram =
    copy(parameters = Parameter.setParameter(parameters, newParameter))
}

object FreestyleProgram extends CirceCodec {
  implicit val codec = semiauto.deriveConfiguredCodec[FreestyleProgram]

  implicit val ordering: Ordering[FreestyleProgram] = Ordering.by[FreestyleProgram, String](_.code)
}
