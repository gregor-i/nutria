package nutria.api

import java.time.{ZoneId, ZoneOffset, ZonedDateTime}

import org.scalatest.funsuite.AnyFunSuite
import io.circe.syntax._
import nutria.CirceCodec

class WithIdTest extends AnyFunSuite with CirceCodec {
  test("codec for WithId[Option[_]]") {
    val now = ZonedDateTime.now(ZoneOffset.UTC)

    val exampleSome = WithId[Option[String]](
      id = "id",
      owner = "owner",
      entity = Some("string"),
      insertedAt = now,
      updatedAt = now
    )

    assert(exampleSome.asJson.as[WithId[Option[String]]] === Right(exampleSome))

    val exampleNone = WithId[Option[String]](
      id = "id",
      owner = "owner",
      entity = None,
      insertedAt = now,
      updatedAt = now
    )

    assert(exampleNone.asJson.as[WithId[Option[String]]] === Right(exampleNone))
  }
}
