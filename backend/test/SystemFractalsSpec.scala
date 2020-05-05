import java.io.FileWriter

import io.circe.syntax._
import nutria.SystemFractals
import nutria.core.FractalEntity
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Try

class SystemFractalsSpec extends AnyFunSuite {

  def read: Vector[FractalEntity] = SystemFractals.systemFractals

  def write(fractals: Vector[FractalEntity]): Unit = {
    val fw = new FileWriter("../core/src/main/data/systemfractals.json")
    fw.append(fractals.sorted.asJson.spaces2SortKeys)
    fw.close()
  }

  test("SystemFractals can read the json file") {
    assert(read.nonEmpty)
  }

  test("SystemFractals are encoded with the Encoder") {
    assert(write(read) === ())
  }
}
