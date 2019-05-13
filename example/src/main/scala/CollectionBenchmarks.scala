package bench.example

import scala.concurrent.duration._
import bench._
import bench.util._

import scala.collection.mutable

object CollectionBenchmarks {
  val linearScan = Comparison("Linear Scan", Seq(
    Benchmark[mutable.HashSet[Int]](
      "mutable set foreach",
      n => new mutable.HashSet[Int]() ++ Array.fill[Int](n)(rInt),
      { set =>
        var sum: Int = 0
        set.foreach(sum += _)
        sum
      }
    ),
    Benchmark[Set[Int]](
      "immutable set foreach",
      n => Array.fill[Int](n)(rInt).toSet,
      { set =>
        var sum: Int = 0
        set.foreach(sum += _)
        sum
      }
    ),
    Benchmark[Array[Int]](
      "array foreach",
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

  val construct = Comparison("Construct", Seq(
    Benchmark[Int](
      "mutable set",
      n => n,
      { n =>
        val set = mutable.HashSet[String]()
        var i = 0
        while (i < n) {
          set += i.toString
          i += 1
        }
        set
      }
    ),
    Benchmark[Int](
      "immutable set",
      n => n,
      { n =>
        var set = Set[String]()
        var i = 0
        while (i < n) {
          set += i.toString
          i += 1
        }
        set
      }
    ),
    Benchmark[Int](
      "array",
      n => n,
      { n =>
        val arr = new Array[String](n)
        var i = 0
        while (i < n) {
          arr(i) = i.toString
          i += 1
        }
        arr
      }
    ),
    Benchmark[Int](
      "arraybuffer",
      n => n,
      { n =>
        val arr = mutable.ArrayBuffer[String]()
        var i = 0
        while (i < n) {
          arr += i.toString
          i += 1
        }
        arr
      }
    )
  ))

  val map = Comparison("Map", Seq(
    Benchmark[mutable.HashSet[Int]](
      "mutable set",
      n => new mutable.HashSet[Int]() ++ Array.fill[Int](n)(rInt),
      { set =>
        set.map(_ * 10)
      }
    ),
    Benchmark[Set[Int]](
      "immutable set",
      n => Array.fill[Int](n)(rInt).toSet,
      { set =>
        set.map(_ * 10)
      }
    ),
    Benchmark[Array[Int]](
      "array",
      n => Array.fill[Int](n)(rInt),
      { (arr) =>
        arr.map(_ * 10)
      }
    ),
    Benchmark[Array[Int]](
      "array while",
      n => Array.fill(n)(rInt),
      { (arr) =>
        val arr2 = new Array[Int](arr.length)
        var i = 0
        while (i < arr.length) {
          arr2(i) += arr(i) * 10
          i += 1
        }
        arr
      }
    )
  ))

  val lookup = Comparison("Lookup", Seq(
    Benchmark[(Int, mutable.HashSet[Int])](
      "mutable set",
      n => (n, new mutable.HashSet[Int]() ++ Array.range(0, n)),
      { case (n, set) =>
        set.contains(n)
      }
    ),
    Benchmark[(Int, Set[Int])](
      "immutable set",
      n => (n, Array.range(0, n).toSet),
      { case (n, set) =>
        set.contains(n)
      }
    ),
    Benchmark[(Int, Array[Int])](
      "array contains",
      n => (n, Array.range(0, n)),
      { case (n, arr) =>
        arr.contains(n)
      }
    ),
    Benchmark[(Int, Array[Int])](
      "array exists",
      n => (n, Array.range(0, n)),
      { case (n, arr) =>
        arr.exists(_ == n)
      }
    ),
    Benchmark[(Int, Array[Int])](
      "array while",
      n => (n, Array.range(0, n)),
      { case (n, arr) =>
        var i = 0
        var found = false
        while (!found && i < arr.length) {
          if (arr(i) == n) found = true
          i += 1
        }
        found
      }
    )
  ))
}
