package bench.example

import scala.concurrent.duration._
import bench.util._

object Main {
  def main(args: Array[String]): Unit = {
    runComparison(CollectionBenchmarks.linearScan, expRange(1000000, 100), 60 seconds)

    ()
  }
}
