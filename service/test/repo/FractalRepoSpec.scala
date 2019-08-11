package repo

import module.SystemFractals
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class FractalRepoSpec extends FunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  def repo = app.injector.instanceOf[FractalRepo]
  val systemFractals = app.injector.instanceOf[SystemFractals]

  val f1 = FractalRow(
    id = "1",
    maybeFractal = Some(systemFractals.systemFractals(0))
  )

  val f2 = FractalRow(
    id = "2",
    maybeFractal = Some(systemFractals.systemFractals(1))
  )

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

  test("delete") {
    repo.save(f1)
    repo.save(f2)
    repo.delete(f1.id)
    repo.get(f1.id) shouldBe None
    repo.list() shouldBe List(f2)
  }
}
