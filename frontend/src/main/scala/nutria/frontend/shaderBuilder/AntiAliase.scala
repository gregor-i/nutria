package nutria.frontend.shaderBuilder

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive

object AntiAliase {
  def apply[T <: WebGlType: WebGlType.TypeProps](
      block: (RefVec2, Ref[T]) => String,
      aaFactor: Int Refined Positive
  ): Ref[T] => String =
    if (aaFactor.value > 1)
      antiAliase(block, aaFactor)
    else
      noAntiAliase(block)

  private def noAntiAliase[T <: WebGlType: WebGlType.TypeProps](
      block: (RefVec2, Ref[T]) => String
  )(outputVar: Ref[T]) =
    s"""{
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |  vec2 p = u_view_O + pos.x * u_view_A + pos.y * u_view_B;
       |  ${block(RefVec2("p"), outputVar)}
       |}
     """.stripMargin

  private def antiAliase[T <: WebGlType: WebGlType.TypeProps](
      block: (RefVec2, Ref[T]) => String,
      aaFactor: Int Refined Positive
  )(outputVarname: Ref[T]) = {
    val local = WebGlType.reference[T]("frag_out")
    val acc   = WebGlType.reference[T]("acc")
    s"""{
       |  vec2 aa_factor = 1.0 / (float(${aaFactor.value}) * u_resolution);
       |  float aa_offset = float(${(1 - aaFactor.value) / 2.0});
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |
       |  ${WebGlType.declare(acc, WebGlType.zero[T])};
       |  for(int aa_x = 0; aa_x < ${aaFactor.value}; aa_x ++){
       |    for(int aa_y = 0; aa_y < ${aaFactor.value}; aa_y ++){
       |      vec2 aa_pos = pos + vec2(float(aa_x) + aa_offset, float(aa_y) + aa_offset) * aa_factor;
       |      vec2 p = u_view_O + aa_pos.x * u_view_A + aa_pos.y * u_view_B;
       |
       |      ${WebGlType.declare(local, WebGlType.zero[T])}
       |      ${block(RefVec2("p"), local)}
       |
       |      acc += ${local.name};
       |    }
       |  }
       |
       |  ${outputVarname.name} = acc / float(${aaFactor.value * aaFactor.value});
       |}
     """.stripMargin
  }

}
