package repo

import java.util.UUID

import nutria.core.{Examples, FractalEntity, Viewport, ViewportList}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class FractalRepoSpec extends AnyFunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  def repo = app.injector.instanceOf[FractalRepo]

  def row(fractal: FractalEntity) =
    FractalRow(
      id = UUID.randomUUID().toString,
      owner = UUID.randomUUID().toString,
      published = fractal.published,
      maybeFractal = Some(fractal)
    )

  val f1 = row(
    FractalEntity(
      program = Examples.timeEscape,
      views = ViewportList.refineUnsafe(List(Viewport.mandelbrot))
    )
  )

  val f2 = row(
    FractalEntity(
      program = Examples.newtonIteration,
      views = ViewportList.refineUnsafe(List(Viewport.aroundZero))
    )
  )

  override def beforeEach = {
    repo.list().foreach(row => repo.delete(row.id))
  }

  test("save & get") {
    repo.save(f1.id, f1.owner, f1.maybeFractal.get)
    repo.save(f2.id, f2.owner, f2.maybeFractal.get)
    assert(repo.get(f1.id) === Some(f1))
    assert(repo.get(f2.id) === Some(f2))
  }

  test("list") {
    repo.save(f1.id, f1.owner, f1.maybeFractal.get)
    repo.save(f2.id, f2.owner, f2.maybeFractal.get)
    repo.list() shouldBe List(f1, f2)
  }

  test("listPublic") {
    repo.save(f1.id, f1.owner, f1.maybeFractal.get.copy(published = true))
    repo.save(f2.id, f2.owner, f2.maybeFractal.get.copy(published = false))
    val Seq(found) = repo.listPublic().collect(repo.fractalRowToFractalEntity)
    found.id shouldBe f1.id
    found.owner shouldBe f1.owner
    found.entity shouldBe f1.maybeFractal.get.copy(published = true)
  }

  test("listByUser") {
    val owner1 = UUID.randomUUID().toString
    val owner2 = UUID.randomUUID().toString
    repo.save(f1.id, owner1, f1.maybeFractal.get)
    repo.save(f2.id, owner2, f2.maybeFractal.get)
    repo.listByUser(owner1) shouldBe List(f1.copy(owner = owner1))
    repo.listByUser(owner2) shouldBe List(f2.copy(owner = owner2))
  }

  test("delete") {
    repo.save(f1.id, f1.owner, f1.maybeFractal.get)
    repo.save(f2.id, f2.owner, f2.maybeFractal.get)
    repo.delete(f1.id)
    repo.get(f1.id) shouldBe None
    repo.list() shouldBe List(f2)
  }
}
