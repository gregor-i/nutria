package nutria.macros

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.util.Using

private[macros] class StaticContentMacro(val c: blackbox.Context) {
  import c._
  import universe._
  def staticFileContent(file: c.Expr[String]): c.universe.Tree = file.tree match {
    case Literal(Constant(s: String)) =>
      val res = Using(Source.fromFile(s, "UTF-8")) { source =>
        source.getLines.mkString("\n")
      }
      q"${res.get}"
  }
}
