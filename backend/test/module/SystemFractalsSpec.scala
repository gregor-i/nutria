package module

import java.io.FileWriter

import io.circe.syntax._
import nutria.core.FractalEntity
import org.scalatest.FunSuite

class SystemFractalsSpec extends FunSuite {

  def read: Vector[FractalEntity] = (new module.SystemFractals).systemFractals

  def write(fractals: Vector[FractalEntity]): Unit = {
    val fw = new FileWriter("conf/systemfractals.json")
    fw.append(fractals.sorted.asJson.spaces2SortKeys)
    fw.close()
  }

  test("SystemFractals can read the json file") {
    read
  }

  test("SystemFractals are encoded with the Encoder") {
    write((new module.SystemFractals).systemFractals)
  }
}
