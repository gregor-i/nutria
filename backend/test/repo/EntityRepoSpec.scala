package repo

import java.time.ZonedDateTime
import java.util.UUID

import nutria.api.{Entity, WithId}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application

abstract class EntityRepoSpec[E <: Entity[_]](repoGetter: Application => EntityRepo[E], e1: E, e2: E)
    extends AnyFunSuite
    with Matchers
    with GuiceOneAppPerSuite
    with BeforeAndAfterEach {
  val repo = repoGetter(app)

  def row(entity: E) =
    WithId(
      id = UUID.randomUUID().toString,
      owner = UUID.randomUUID().toString,
      entity = entity,
      updatedAt = ZonedDateTime.now(),
      insertedAt = ZonedDateTime.now()
    )

  val f1 = row(e1)
  val f2 = row(e2)

  override def beforeEach(): Unit = {
    repo.list().foreach(row => repo.delete(row.id))
  }

  test("save & get") {
    val saved = repo.save(f1.id, f1.owner, f1.entity): WithId[E]
    val got   = repo.get(f1.id): Option[WithId[Option[E]]]

    assert(saved.owner === f1.owner)
    assert(saved.entity === f1.entity)

    assert(got.isDefined)
    assert(got.get.owner === f1.owner)
    assert(got.get.entity.get === f1.entity)
  }

  test("list") {
    repo.save(f1.id, f1.owner, f1.entity)
    repo.save(f2.id, f2.owner, f2.entity)
    assert(repo.list().map(_.id) === List(f1.id, f2.id))
  }

  test("delete") {
    repo.save(f1.id, f1.owner, f1.entity)
    repo.save(f2.id, f2.owner, f2.entity)
    repo.delete(f1.id)
    assert(repo.get(f1.id) === None)
    assert(repo.list().map(_.id) === List(f2.id))
  }

  test("updatedAt ist automatically increased") {
    val saved = repo.save(f1.id, f1.owner, f1.entity)
    Thread.sleep(10)
    val updated = repo.save(f1.id, f1.owner, f1.entity)

    assert(saved.entity === updated.entity)
    assert(saved.owner === updated.owner)
    assert(saved.insertedAt === updated.insertedAt)
    assert(saved.updatedAt.isBefore(updated.updatedAt))
  }
}
