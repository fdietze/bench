package bench

import scala.concurrent.duration._
import flatland._
import collection.breakOut

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

  @inline def now = PlatformNow.nanoTime

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

  def medianFromSorted(sortedData: IndexedSeq[Long]): Double = {
    // https://rcoh.me/posts/linear-time-median-finding/
    val n = sortedData.length
    if (n % 2 == 1) sortedData(n / 2)
    else (sortedData(n / 2 - 1) + sortedData(n / 2)) / 2.0
  }

  def medianFromSortedDouble(sortedData: IndexedSeq[Double]): Double = {
    // https://rcoh.me/posts/linear-time-median-finding/
    val n = sortedData.length
    if (n % 2 == 1) sortedData(n / 2)
    else (sortedData(n / 2 - 1) + sortedData(n / 2)) / 2.0
  }

  def madFromSorted(sortedData: IndexedSeq[Long]): Double = {
    val median = medianFromSorted(sortedData).toDouble
    val sortedDifferences = (sortedData.map(x => (x - median).abs)(breakOut): Array[Double]).sorted
    medianFromSortedDouble(sortedDifferences)
  }

  def bootstrappingCI(population: IndexedSeq[Long], confidence: Double = 0.95, m: Int = 10000, k: Int = 100000): (Double, Double, Double) = {
    val n = population.length
    val means = new Array[Double](k)
    loop(k) { i =>
      val subsamples = Array.fill(m)(population(scala.util.Random.nextInt(n)))
      val mean = subsamples.sum.toDouble / m
      means(i) = mean
    }
    val sortedMeans = means.sorted
    (
      sortedMeans((k * (1 - confidence)).toInt),
      sortedMeans(k / 2),
      sortedMeans((k * confidence).toInt)
    )
  }

  // @inline, such that code hopefully gets inlined
  @inline def repeatCodeUntilConfident(maxSamples: Int = 1000000, error: Double = 0.01, zStar: Double = 1.96)(code: => Any): Duration = {
    import Math.sqrt
    // https://www.mathsisfun.com/data/confidence-interval.html
    // https://www.ucl.ac.uk/child-health/short-courses-events/about-statistical-courses/research-methods-and-statistics/chapter-8-content-8
    // https://htor.inf.ethz.ch/blog/index.php/2016/04/14/how-many-measurements-do-you-need-to-report-a-performance-number/

    val samples = ArrayQueueLong.create(maxSamples)
    // var sortedSamples: IndexedSeq[Long] = IndexedSeq.empty
    // def median = medianFromSorted(sortedSamples)
    // def MAD = madFromSorted(sortedSamples)
    // var totalDuration = 0L
    var totalSamples = 0L
    // def lowerConfidenceLimit = sortedSamples((samples.size / 2.0 - zStar * sqrt(samples.size) / 2.0).toInt)
    // def upperConfidenceLimit = sortedSamples((1.0 + samples.size / 2.0 + zStar * sqrt(samples.size) / 2.0).toInt)

    @inline def now: Long = System.nanoTime()
    var start = 0L
    @inline def measure(code: => Any) = {
      start = now
      code
      samples += (now - start)
    }

    while (totalSamples < 10001) {
      measure{
        code
      }
      // println(s"i: ${totalSamples}")
      if (totalSamples > 30 && totalSamples % 10000 == 0) {
        println(samples)
        val (low, median, high) = bootstrappingCI(samples)
        println(f"i: ${totalSamples}%05d, n: ${samples.length}, median: $median%.2f, 95%% confidence Interval: [$low%.2f,$high%.2f] [${median - low}%.2f,${high - median}%.2f] size: ${high-low}%.2f")
      }
      totalSamples += 1
    }

    val (low, median, high) = bootstrappingCI(samples)
    Duration.fromNanos(median)
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
