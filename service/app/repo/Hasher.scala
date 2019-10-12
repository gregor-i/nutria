package repo

import java.security.MessageDigest

object Hasher {
  val digest = MessageDigest.getInstance("SHA-1")

  def apply(bytes: Array[Byte]): String =
    digest
      .digest(bytes)
      .map("%02x".format(_)).mkString
}
