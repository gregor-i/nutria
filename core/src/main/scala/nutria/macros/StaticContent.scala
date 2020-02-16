package nutria.macros

import scala.language.experimental.macros

object StaticContent {
  def apply(file: String): String = macro StaticContentMacro.staticFileContent
}
