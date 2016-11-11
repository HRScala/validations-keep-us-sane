package hrscala.validation

import org.openjdk.jmh.annotations.Benchmark

class ScalacticBenchmark {
  @Benchmark
  def range(): Int =
    1.to(100000)
      .filter(_ % 2 == 0)
      .count(_.toString.length == 4)
}
