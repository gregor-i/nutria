package nutria.benchmark.cardioid

import java.util.Random

import nutria.core.consumers.CardioidNumeric
import org.openjdk.jmh.annotations.Benchmark

class Golden {

  @Benchmark
  def loop = {
    val random = new Random
    val x = random.nextGaussian()
    val y = random.nextGaussian()
    CardioidNumeric.golden(100)(x, y)
  }

  @Benchmark
  def rec = {
    val random = new Random
    val x = random.nextGaussian()
    val y = random.nextGaussian()
    CardioidNumeric.goldenRec(100)(x, y)
  }
}
