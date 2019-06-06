package nutria.frontend.shaderBuilder

object AntiAliase {
  def apply(block: (String, String) => String, aaFactor: Int)(outputVarName: String) =
    if(aaFactor > 1)
      antiAliase(block, aaFactor)(outputVarName)
    else
      noAntiAliase(block)(outputVarName)

  private def noAntiAliase(block: (String, String) => String)(outputVarname: String) =
    s"""{
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |  vec2 p = u_view_O + pos * u_view_A + pos.y * u_view_B;
       |  ${block("p", outputVarname)}
       |}
     """.stripMargin

  private def antiAliase(block: (String, String) => String, aaFactor: Int)(outputVarname: String) =
    s"""{
       |  vec2 aa_factor = 1.0 / (float($aaFactor) * u_resolution);
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |
       |  vec4 acc = vec4(0.0, 0.0, 0.0, 0.0);
       |  for(int aa_x = 0; aa_x < $aaFactor; aa_x ++){
       |    for(int aa_y = 0; aa_y < $aaFactor; aa_y ++){
       |      vec2 aa_pos = pos + vec2(float(aa_x), float(aa_y)) * aa_factor;
       |      vec2 p = u_view_O + aa_pos * u_view_A + aa_pos.y * u_view_B;
       |
       |      vec4 frag_out = vec4(0.0, 0.0, 0.0, 0.0);
       |      ${block("p", "frag_out")}
       |
       |      acc += frag_out;
       |    }
       |  }
       |
       |  $outputVarname = acc / float(${aaFactor * aaFactor});
       |}
     """.stripMargin

}
