package repo

import java.util.UUID

import nutria.api.{Entity, FractalTemplateEntity, WithId}
import nutria.core.{Examples, Viewport, ViewportList}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class TemplateRepoSpec extends AnyFunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  def repo = app.injector.instanceOf[TemplateRepo]

  def row(template: FractalTemplateEntity) =
    WithId(
      id = UUID.randomUUID().toString,
      owner = UUID.randomUUID().toString,
      entity = Some(template)
    )

  val testData = Examples.allNamed
    .map {
      case (name, template) =>
        Entity(title = name, value = template)
    }
    .map(row)

  val f1 = testData(0)

  val f2 = testData(1)

  override def beforeEach = {
    repo.list().foreach(row => repo.delete(row.id))
  }

  test("save & get") {
    repo.save(f1.id, f1.owner, f1.entity.get)
    repo.save(f2.id, f2.owner, f2.entity.get)
    assert(repo.get(f1.id) === Some(f1))
    assert(repo.get(f2.id) === Some(f2))
  }

  test("list") {
    repo.save(f1.id, f1.owner, f1.entity.get)
    repo.save(f2.id, f2.owner, f2.entity.get)
    repo.list() shouldBe List(f1, f2)
  }

  test("delete") {
    repo.save(f1.id, f1.owner, f1.entity.get)
    repo.save(f2.id, f2.owner, f2.entity.get)
    repo.delete(f1.id)
    repo.get(f1.id) shouldBe None
    repo.list() shouldBe List(f2)
  }
}
