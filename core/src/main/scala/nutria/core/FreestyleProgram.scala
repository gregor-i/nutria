package nutria.core

@monocle.macros.Lenses()
case class FreestyleProgram(code: String, parameters: Vector[Parameter] = Vector.empty)

object FreestyleProgram extends CirceCodec {
  implicit val codec = semiauto.deriveConfiguredCodec[FreestyleProgram]

  implicit val ordering: Ordering[FreestyleProgram] = Ordering.by[FreestyleProgram, String](_.code)
}
