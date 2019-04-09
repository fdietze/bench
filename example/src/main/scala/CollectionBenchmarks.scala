package bench.example

import scala.concurrent.duration._
import bench._
import bench.util._

object CollectionBenchmarks {
  val linearScan = Comparison("Linear Scan", Seq(
    Benchmark[Array[Int]](
      "int array foreach",
      n => Array.fill[Int](n)(rInt),
      { (arr) =>
        var sum: Int = 0
        arr.foreach(sum += _)
        sum
      }
    ),
    Benchmark[Array[Int]](
      "array while",
      n => Array.fill(n)(rInt),
      { (arr) =>
        var sum: Int = 0
        var i = 0
        while (i < arr.length) {
          sum += arr(i)
          i += 1
        }
        sum
      }
    )
  ))
}
