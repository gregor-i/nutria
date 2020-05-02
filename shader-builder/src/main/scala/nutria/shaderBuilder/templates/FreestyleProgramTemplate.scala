package nutria.shaderBuilder.templates

import nutria.core.FreestyleProgram
import nutria.shaderBuilder.{RefVec2, RefVec4}

object FreestyleProgramTemplate extends Template[FreestyleProgram] {
  override def constants(v: FreestyleProgram): Seq[String] = Seq.empty

  override def functions(v: FreestyleProgram): Seq[String] = Seq.empty

  override def main(v: FreestyleProgram)(inputVar: RefVec2, outputVar: RefVec4): String = {
    val code = v.parameters.foldLeft(v.code) { (template, parameter) =>
      template.replaceAllLiterally("${" + parameter.name + "}", parameter.literal)
    }

    s"""{
     |vec2 z = ${inputVar.name};
     |vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
     |
     |$code
     |
     |${outputVar.name} = color;
     |}
     """.stripMargin
  }
}
