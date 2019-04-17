package bench

import scala.concurrent.duration._

package object util {
  @inline def rInt: Int = scala.util.Random.nextInt
  @inline def rDouble: Double = scala.util.Random.nextDouble
  @inline def repeatCode[T](n: Long)(code: => T): T = {
    var last: T = code
    var i = 1
    while (i < n) {
      last = code
      i += 1
    }
    last
  }

  @inline def whilei(n: Int)(code: Int => Any): Unit = {
    var i = 0
    while (i < n) {
      code(i)
      i += 1
    }
  }

  def expRange(max: Int, base: Int = 2) = {
    require(max <= (1 << 30))
    List.tabulate[Int]((Math.log(max) / Math.log(base)).ceil.toInt + 1)(i => Math.pow(base, i).toInt)
  }

  @inline def repeatCodeFor[T](minDuration: Duration)(code: => T): (Duration, Long) = {
    @inline def now: Long = System.nanoTime()
    val durationNs = minDuration.toNanos
    var count: Long = 0

    val start = now
    @inline def passed = now - start

    while (passed < durationNs) {
      code
      count += 1
    }
    val end = now
    val total = end - start
    (Duration.fromNanos(total), count)
  }

  val defaultWarmup = 2
  def benchmarkSeries(benchmark: BenchmarkLike[_], dataSizes: Seq[Int], duration: Duration, warmup: Int = defaultWarmup): Seq[(Int, Duration)] = {
    val seriesDuration = duration / (warmup + 1) // keep only one result
    def runSeries = {
      dataSizes.map { dataSize =>
        // println(s"dataSize: $dataSize")
        val avgDuration = benchmark.runFor(dataSize, minDuration = seriesDuration / dataSizes.length)
        dataSize -> avgDuration.max(1 nanoseconds)
      }
    }
    // println("warmup...")
    repeatCode(warmup) { runSeries } // drop results for warmup
    // println("real run")
    runSeries // only take one final result
  }

  val numPad = 15
  def runComparison(comparison: Comparison, sizes: Seq[Int], duration: Duration, warmup: Int = defaultWarmup): (String, Seq[(String, Seq[(Int, Duration)])]) = {
    val namePad = comparison.benchmarks.map(_.name.length).max
    val durationForSingleRun = (duration / comparison.benchmarks.size / (warmup + 1)) / sizes.size
    println("Comparison Benchmark:  " + comparison.name)
    println("Duration total:        " + duration.toMillis + "ms")
    println("Duration per run:      " + durationForSingleRun.toMillis + "ms")
    println("(result durations in nanoseconds)")
    println(s"${" " * namePad}${sizes.map(s => s"%${numPad}d" format s).mkString}")
    val benchmarkDuration = duration / comparison.benchmarks.size
    val result = comparison.name -> comparison.benchmarks.map{ benchmark =>
      val seriesResult = benchmarkSeries(benchmark, sizes, benchmarkDuration, warmup)
      println(benchmark.name.replace(" ", "_").padTo(namePad, " ").mkString + seriesResult.map{ case (_, duration) => s"%${numPad}d" format duration.toNanos }.mkString)
      benchmark.name -> seriesResult
    }
    println()
    result
  }
}
