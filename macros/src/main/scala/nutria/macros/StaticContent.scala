package nutria.macros

import scala.language.experimental.macros
import scala.io.Source
import scala.reflect.macros.blackbox
import scala.util.Using

object StaticContent {
  def apply(file: String): String = macro StaticContentMacro.staticFileContent
}

private[macros] class StaticContentMacro(val c: blackbox.Context) {
  import c._
  import universe._
  def staticFileContent(file: c.Expr[String]): c.universe.Tree = file.tree match {
    case Literal(Constant(s: String)) =>
      val res = Using(Source.fromFile(s, "UTF-8"))(_.getLines().mkString("\n"))
      q"${res.get}"
  }
}
