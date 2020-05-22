package nutria.core.languages

import io.circe.{Decoder, Encoder}
import mathParser.implicits._
import nutria.CirceCodec

class StringFunction[V] private (val string: String, val node: CNode[V]) {
  override def equals(other: Any): Boolean =
    other match {
      case o: StringFunction[V] => this.string == o.string && this.node == o.node
      case _                    => false
    }

  override def hashCode(): Int = string.hashCode ^ node.hashCode()

  override def toString: String = string
}

object StringFunction extends CirceCodec {
  def apply[V](string: String)(implicit lang: CLang[V]): Option[StringFunction[V]] =
    lang.parse(string).map(lang.optimize).map(node => new StringFunction(string, node))

  def unsafe[V](string: String)(implicit lang: CLang[V]): StringFunction[V] =
    apply[V](string).get

  implicit def encoder[V]: Encoder[StringFunction[V]] = Encoder[String].contramap(_.string)
  implicit def decoder[V](implicit lang: CLang[V]): Decoder[StringFunction[V]] =
    Decoder[String].flatMap { string =>
      apply[V](string) match {
        case Some(stringFunction) => Decoder.const(stringFunction)
        case None                 => Decoder.failedWithMessage(s"function '$string' could not be parsed")
      }
    }
}
