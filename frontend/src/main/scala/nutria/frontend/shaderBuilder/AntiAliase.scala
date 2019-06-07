package nutria.frontend.shaderBuilder

import nutria.frontend.shaderBuilder.Ref.RefProps

object AntiAliase {
  def apply[R <: Ref : RefProps](block: (RefVec2, R) => String, aaFactor: Int)(outputVar: R) =
    if(aaFactor > 1)
      antiAliase(block, aaFactor)(outputVar)
    else
      noAntiAliase(block)(outputVar)

  private def noAntiAliase[R <: Ref : RefProps](block: (RefVec2, R) => String)(outputVar: R) =
    s"""{
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |  vec2 p = u_view_O + pos * u_view_A + pos.y * u_view_B;
       |  ${block(RefVec2("p"), outputVar)}
       |}
     """.stripMargin

  private def antiAliase[R <: Ref : RefProps](block: (RefVec2, R) => String, aaFactor: Int)(outputVarname: R) = {
    val local = Ref.construct[R]("frag_out")
    s"""{
       |  vec2 aa_factor = 1.0 / (float($aaFactor) * u_resolution);
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |
       |  ${Ref.webGlType[R]} acc = ${Ref.unit[R]};
       |  for(int aa_x = 0; aa_x < $aaFactor; aa_x ++){
       |    for(int aa_y = 0; aa_y < $aaFactor; aa_y ++){
       |      vec2 aa_pos = pos + vec2(float(aa_x), float(aa_y)) * aa_factor;
       |      vec2 p = u_view_O + aa_pos * u_view_A + aa_pos.y * u_view_B;
       |
       |      ${Ref.declare(local)}
       |      ${block(RefVec2("p"), local)}
       |
       |      acc += ${local.name};
       |    }
       |  }
       |
       |  ${outputVarname.name} = acc / float(${aaFactor * aaFactor});
       |}
     """.stripMargin
  }

}
