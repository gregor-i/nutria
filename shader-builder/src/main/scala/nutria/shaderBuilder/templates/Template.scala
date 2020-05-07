package nutria.shaderBuilder.templates

trait Template[V] {
  def definitions(v: V): Seq[String]

  def main(v: V): String
}
