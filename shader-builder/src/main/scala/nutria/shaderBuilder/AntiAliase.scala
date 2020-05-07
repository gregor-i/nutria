package nutria.shaderBuilder

import nutria.core.AntiAliase

object AntiAliase {
  def apply(aaFactor: AntiAliase): RefVec4 => String =
    if (aaFactor.value > 1)
      antiAliase(aaFactor)
    else
      noAntiAliase

  private def noAntiAliase(outputVar: RefVec4) =
    s"""
       |vec2 pos = gl_FragCoord.xy / u_resolution;
       |vec2 p = u_view_O + pos.x * u_view_A + pos.y * u_view_B;
       |${outputVar.name} = main_template(p);
     """.stripMargin

  private def antiAliase(aaFactor: AntiAliase)(outputVarname: RefVec4) = {
    s"""
       |const int aa = ${aaFactor.value};
       |vec2 aa_factor = 1.0 / (float(aa) * u_resolution);
       |vec2 aa_offset = vec2((1.0 - float(aa)) / 2.0) * aa_factor;
       |vec2 pos = gl_FragCoord.xy / u_resolution;
       |
       |vec4 acc = vec4(0.0);
       |for(int aa_x = 0; aa_x < aa; aa_x ++){
       |  for(int aa_y = 0; aa_y < aa; aa_y ++){
       |    vec2 aa_pos = pos + vec2(float(aa_x), float(aa_y)) * aa_factor + aa_offset;
       |    vec2 p = u_view_O + aa_pos.x * u_view_A + aa_pos.y * u_view_B;
       |
       |    acc += main_template(p);
       |  }
       |}
       |
       |${outputVarname.name} = acc / (float(aa) * float(aa));
     """.stripMargin
  }

}
