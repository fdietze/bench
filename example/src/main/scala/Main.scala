package bench.example

import scala.concurrent.duration._
import bench.util._

object Main {
  def main(args: Array[String]): Unit = {
    runComparison(SleepBenchmarks.linearScan, List(1, 10, 100), iterations = 10, 60 seconds)
    runComparison(CollectionBenchmarks.linearScan, expRange(1000), iterations = 1000, 30 seconds)

    ()
  }
}

