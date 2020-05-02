package nutria.shaderBuilder.templates

import nutria.shaderBuilder.{RefVec2, RefVec4}

trait Template[V] {
  def constants(v: V): Seq[String]

  def functions(v: V): Seq[String]

  def main(v: V)(inputVar: RefVec2, outputVar: RefVec4): String
}
