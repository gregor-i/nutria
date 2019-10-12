package repo

import module.SystemFractals
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.util.Random

import scala.util.chaining._

class FractalImageRepoSpec extends FunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val fractalRepo = app.injector.instanceOf[FractalRepo]
  val imageRepo = app.injector.instanceOf[FractalImageRepo]
  val systemFractals = app.injector.instanceOf[SystemFractals]

  val f1 = FractalRow(
    id = "1",
    maybeFractal = Some(systemFractals.systemFractals(0))
  )

  val bytes1 = new Array[Byte](1000)
      .tap(Random.nextBytes)
  val hash1 = Hasher(bytes1)

  val bytes2 = new Array[Byte](1000*1000)
    .tap(Random.nextBytes)
  val hash2 = Hasher(bytes2)

  override def beforeEach = {
    fractalRepo.save(f1)
    imageRepo.truncate()
  }

  test("save") {
    imageRepo.getImage(f1.id) shouldBe None

    imageRepo.save(f1.id, hash1, bytes1)
    imageRepo.getHash(f1.id) shouldBe Some(hash1)
    imageRepo.getImage(f1.id).map(_.toSeq) shouldBe Some(bytes1.toSeq)

    imageRepo.save(f1.id, hash2, bytes2)
    imageRepo.getHash(f1.id) shouldBe Some(hash2)
    imageRepo.getImage(f1.id).map(_.toSeq) shouldBe Some(bytes2.toSeq)
  }

  test("truncate"){
    imageRepo.save(f1.id, hash1, bytes1)
    imageRepo.truncate()
    imageRepo.getImage(f1.id) shouldBe None
  }
}
