package nutria.frontend

import org.scalajs.dom

object DecodeCookie {

  private def cookieRegex(name: String) = s".*${name}=([a-zA-Z0-9+/]+={0,2}).*".r

  def apply[A: io.circe.Decoder](name: String): Option[A] = {
    val Regex = cookieRegex(name)
    val rawValue = dom.document.cookie match {
      case Regex(value) => Some(value)
      case _ => None
    }
    rawValue.map(dom.window.atob)
      .flatMap(io.circe.parser.parse(_).toOption)
      .flatMap(_.as[A].toOption)
  }
}
