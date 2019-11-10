package repo

import java.util.UUID

import module.SystemFractals
import nutria.core.FractalEntity
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class FractalRepoSpec extends FunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  def repo = app.injector.instanceOf[FractalRepo]
  val systemFractals = app.injector.instanceOf[SystemFractals]

  def row(fractal: FractalEntity) =
    FractalRow(
      id = UUID.randomUUID().toString,
      owner = None,
      published = false,
      maybeFractal = Some(fractal)
    )

  val f1 = row(systemFractals.systemFractals(0))
  val f2 = row(systemFractals.systemFractals(1))

  override def beforeEach = {
    repo.list().foreach(row => repo.delete(row.id))
  }

  test("save") {
    repo.save(f1)
    repo.save(f2)
  }

  test("get") {
    repo.save(f1)
    repo.save(f2)
    repo.get(f1.id) shouldBe Some(f1)
    repo.get(f2.id) shouldBe Some(f2)
  }

  test("list") {
    repo.save(f1)
    repo.save(f2)
    repo.list() shouldBe List(f1, f2)
  }

  test("listPublic") {
    repo.save(f1.copy(published = true))
    repo.save(f2.copy(published = false))
    repo.listPublic() shouldBe List(f1.copy(published = true))
  }

  test("listByUser") {
    val owner1 = UUID.randomUUID().toString
    val owner2 = UUID.randomUUID().toString
    repo.save(f1.copy(owner = Some(owner1)))
    repo.save(f2.copy(owner = Some(owner2)))
    repo.listByUser(owner1) shouldBe List(f1.copy(owner = Some(owner1)))
    repo.listByUser(owner2) shouldBe List(f2.copy(owner = Some(owner2)))
  }

  test("delete") {
    repo.save(f1)
    repo.save(f2)
    repo.delete(f1.id)
    repo.get(f1.id) shouldBe None
    repo.list() shouldBe List(f2)
  }
}
