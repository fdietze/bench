package bench.example

import scala.concurrent.duration._
import bench.util._

object Main {
  def main(args: Array[String]): Unit = {
    // runComparison(CollectionBenchmarks.linearScan, expRange(10000, 100), 60 seconds)
    runComparison(CollectionBenchmarks.hashmap, expRange(10000, 10), 120 seconds)
    runComparison(CollectionBenchmarks.hashmapBuild, expRange(10000, 10), 120 seconds)

    ()
  }
}
