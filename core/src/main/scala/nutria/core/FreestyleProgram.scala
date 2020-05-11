package nutria.core

import io.circe.Decoder

@monocle.macros.Lenses()
case class FreestyleProgram(code: String, parameters: Vector[Parameter] = Vector.empty)

object FreestyleProgram extends CirceCodec {
  val default = FreestyleProgram("result = vec4(abs(z.x), abs(z.y), length(z), 1.0);")

  implicit val encode = semiauto.deriveConfiguredEncoder[FreestyleProgram]

  // todo: after migration, only use default.
  private val autoDecode     = semiauto.deriveConfiguredDecoder[FreestyleProgram]
  private val oldDecode      = autoDecode.at("FreestyleProgram")
  private val decodeOnlyCode = Decoder.decodeString.at("code").at("FreestyleProgram").map(FreestyleProgram(_, Vector.empty))
  implicit val decode = autoDecode
    .or(oldDecode)
    .or(decodeOnlyCode)
//    .or(Decoder.decodeJson.emap[FreestyleProgram] { json =>
//      println(s"could not parse, ${json}")
//      Left[String, FreestyleProgram]("failed")
//    })

  implicit val ordering: Ordering[FreestyleProgram] = Ordering.by[FreestyleProgram, String](_.code)
}
