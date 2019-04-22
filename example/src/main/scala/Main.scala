package bench.example

import scala.concurrent.duration._
import bench.util._

object Main {
  def main(args: Array[String]): Unit = {
    // runComparison(CollectionBenchmarks.linearScan, expRange(1000000, 100), 10 seconds)
    bench.BenchmarkWithoutInit("tabulate", { size =>
      Array.tabulate(size)(i => i)
    }).runUntilConfident(1000)

    ()
  }
}
