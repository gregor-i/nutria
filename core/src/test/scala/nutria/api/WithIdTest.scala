package nutria.api

import org.scalatest.funsuite.AnyFunSuite
import io.circe.syntax._
import nutria.CirceCodec

class WithIdTest extends AnyFunSuite with CirceCodec {
  test("codec for WithId[Option[_]]") {
    val exampleSome = WithId[Option[String]](
      id = "id",
      owner = "owner",
      entity = Some("string")
    )

    assert(exampleSome.asJson.as[WithId[Option[String]]] === Right(exampleSome))

    val exampleNone = WithId[Option[String]](
      id = "id",
      owner = "owner",
      entity = None
    )

    assert(exampleNone.asJson.as[WithId[Option[String]]] === Right(exampleNone))
  }
}
