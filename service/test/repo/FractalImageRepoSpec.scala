package repo

import module.SystemFractals
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.util.Random

class FractalImageRepoSpec extends FunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val fractalRepo = app.injector.instanceOf[FractalRepo]
  val imageRepo = app.injector.instanceOf[FractalImageRepo]
  val systemFractals = app.injector.instanceOf[SystemFractals]

  val f1 = FractalRow(
    id = "1",
    maybeFractal = Some(systemFractals.systemFractals(0))
  )

  val f2 = FractalRow(
    id = "2",
    maybeFractal = Some(systemFractals.systemFractals(1))
  )

  val bytes1 = new Array[Byte](1000)
  Random.nextBytes(bytes1)

  val bytes2 = new Array[Byte](1000*1000)
  Random.nextBytes(bytes2)

  override def beforeEach = {
    fractalRepo.save(f1)
    fractalRepo.save(f2)
  }

  test("save") {
    imageRepo.save(f1.id, bytes1)
    imageRepo.save(f2.id, bytes2)
    imageRepo.get(f1.id).map(_.toSeq) shouldBe Some(bytes1).map(_.toSeq)
    imageRepo.get(f2.id).map(_.toSeq) shouldBe Some(bytes2).map(_.toSeq)
  }

  test("truncate"){
    imageRepo.save(f1.id, bytes1)
    imageRepo.save(f2.id, bytes2)
    imageRepo.truncate()
    imageRepo.get(f1.id) shouldBe None
    imageRepo.get(f2.id) shouldBe None
  }
}
