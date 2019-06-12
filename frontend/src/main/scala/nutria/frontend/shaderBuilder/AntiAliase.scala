package nutria.frontend.shaderBuilder

object AntiAliase {
  def apply[T <: WebGlType : WebGlType.TypeProps](block: (RefVec2, Ref[T]) => String, aaFactor: Int)(outputVar: Ref[T]) =
    if(aaFactor > 1)
      antiAliase(block, aaFactor)(outputVar)
    else
      noAntiAliase(block)(outputVar)

  private def noAntiAliase[T <: WebGlType : WebGlType.TypeProps](block: (RefVec2, Ref[T]) => String)(outputVar: Ref[T]) =
    s"""{
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |  vec2 p = u_view_O + pos * u_view_A + pos.y * u_view_B;
       |  ${block(RefVec2("p"), outputVar)}
       |}
     """.stripMargin

  private def antiAliase[T <: WebGlType : WebGlType.TypeProps](block: (RefVec2, Ref[T]) => String, aaFactor: Int)(outputVarname: Ref[T]) = {
    val local = WebGlType.reference[T]("frag_out")
    val acc = WebGlType.reference[T]("acc")
    s"""{
       |  vec2 aa_factor = 1.0 / (float($aaFactor) * u_resolution);
       |  float aa_offset = float(${(1 - aaFactor) / 2.0});
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |
       |  ${WebGlType.declare(acc, WebGlType.zero[T])};
       |  for(int aa_x = 0; aa_x < $aaFactor; aa_x ++){
       |    for(int aa_y = 0; aa_y < $aaFactor; aa_y ++){
       |      vec2 aa_pos = pos + vec2(float(aa_x) + aa_offset, float(aa_y) + aa_offset) * aa_factor;
       |      vec2 p = u_view_O + aa_pos * u_view_A + aa_pos.y * u_view_B;
       |
       |      ${WebGlType.declare(local, WebGlType.zero[T])}
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
