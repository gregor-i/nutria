/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nutria.benchmark

import org.openjdk.jmh.annotations.Benchmark

object ForBenchmark{
  final val n = 10000
  final val cachedRange = 0 until n
}

class ForBenchmark {
  import ForBenchmark._

  @Benchmark
  def defaultScala = {
    var a = 0d
    for (i <- 0 until n)
      a += i
    a
  }

  @Benchmark
  def defaultScalaWithCashedRange = {
    var a = 0d
    for (i <- cachedRange)
      a += i
    a
  }

  @Benchmark
  def spireCFor = {
    import spire.syntax.cfor._
    var a = 0d
    cfor(0)(_ < n, _ + 1) {
      i => a += i
    }
    a
  }
}

/*
unexpected results:
[info] Benchmark                                  Mode  Cnt       Score     Error  Units
[info] ForBenchmark.defaultScala                 thrpt  100  117632,143 ÃƒÂ¢Ã¢â‚¬â€œÃ¢â‚¬â„¢ 580,704  ops/s
[info] ForBenchmark.defaultScalaWithCashedRange  thrpt  100   88782,861 ÃƒÂ¢Ã¢â‚¬â€œÃ¢â‚¬â„¢ 417,568  ops/s
[info] ForBenchmark.spireCFor                    thrpt  100  118483,978 ÃƒÂ¢Ã¢â‚¬â€œÃ¢â‚¬â„¢ 377,722  ops/s
 */