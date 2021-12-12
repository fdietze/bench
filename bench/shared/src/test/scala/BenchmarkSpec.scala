package bench

import minitest._
import scala.concurrent.duration._
import bench.util._

object BenchmarkSuite extends SimpleTestSuite {
  // Javascript seems to have a nanoTime accuracy of 1 millisecond.
  // In this test, this affects the sleep, as well as the measurement (tolerance < 1ms does not pass).

  val sleepUnit: TimeUnit = MICROSECONDS
  val sleepAmount = 1
  val sleepDuration = Duration(sleepAmount, sleepUnit)
  val tolerance = 10.microseconds
  val minRunDuration = 1000.milliseconds

  @inline def sleepFor(duration: Duration): Unit = {
    repeatCodeFor(sleepDuration){
      ()
    }
    ()
  }

  def testBenchmark(b:BenchmarkLike[_]):Unit = {
    val avgDuration = b.runFor(dataSize = 0, minDuration = minRunDuration)
    val expected = sleepDuration
    println((avgDuration - expected).toString)
    assert((avgDuration - expected).toNanos.abs <= tolerance.toNanos, (avgDuration - expected).toString)
  }

  test("BenchmarkWithoutInit sleep") {
    val b = BenchmarkWithoutInit("sleep", size => sleepFor(sleepDuration))
    testBenchmark(b)
  }
  test("Benchmark sleep") {
    val b = Benchmark[Unit](
      "sleep",
      size => sleepFor(4 * sleepDuration),
      _ => sleepFor(sleepDuration)
    )
    testBenchmark(b)
  }

  test("BenchmarkImmutableInit sleep") {
    val b = BenchmarkImmutableInit[Unit](
      "sleep",
      size => sleepFor(4 * sleepDuration),
      _ => sleepFor(sleepDuration)
    )
    testBenchmark(b)
  }
}
