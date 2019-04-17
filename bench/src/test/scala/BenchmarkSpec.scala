package bench

import minitest._
import scala.concurrent.duration._
import bench.util._

object BenchmarkSuite extends SimpleTestSuite {
  test("BenchmarkWithoutInit sleep") {
    val sleep = 30 milliseconds
    val b = BenchmarkWithoutInit("sleep", { size =>
      repeatCodeFor(size milliseconds){
        ()
      }
    })
    val nanos = b.runFor(dataSize = sleep.toMillis.toInt, 1 seconds)._1.toNanos
    val expectedNanos = sleep.toNanos
    val tolerance = (100 microseconds).toNanos
    assert((nanos - expectedNanos).abs <= tolerance, (nanos - expectedNanos).toString)
  }
  test("Benchmark sleep") {
    val sleep = 30 milliseconds
    val b = Benchmark[Int](
      "sleep",
      { size =>
        repeatCodeFor((size * 2) milliseconds){ () }
        size
      },
      { (size) =>
        repeatCodeFor(size milliseconds){
          ()
        }
      }
    )
    val nanos = b.runFor(dataSize = sleep.toMillis.toInt, 1 seconds)._1.toNanos
    val expectedNanos = sleep.toNanos
    val tolerance = (100 microseconds).toNanos
    assert((nanos - expectedNanos).abs <= tolerance, (nanos - expectedNanos).toString)
  }

  test("BenchmarkImmutableInit sleep") {
    val sleep = 30 milliseconds
    val b = BenchmarkImmutableInit[Int](
      "sleep",
      { size =>
        repeatCodeFor((size * 2) milliseconds){ () }
        size
      },
      { (size) =>
        repeatCodeFor(size milliseconds){
          ()
        }
      }
    )
    val nanos = b.runFor(dataSize = sleep.toMillis.toInt, 1 seconds)._1.toNanos
    val expectedNanos = sleep.toNanos
    val tolerance = (100 microseconds).toNanos
    assert((nanos - expectedNanos).abs <= tolerance, (nanos - expectedNanos).toString)
  }
}
