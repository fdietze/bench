package bench.example

import scala.concurrent.duration._
import bench._
import bench.util._

object SleepBenchmarks {
  val linearScan = Comparison("Sleep", Seq(
    // BenchmarkWithoutInit(
    //   "Thread.sleep",
    //   { (size) =>
    //     Thread.sleep(size)
    //   }
    // ),
    Benchmark[Int](
      "runFor",
      { size =>
        runFor((size*2) milliseconds){ () }
        size
      },
      { (size) =>
        runFor(size milliseconds){
          ()
        }
      }
    ),
  ))
}
