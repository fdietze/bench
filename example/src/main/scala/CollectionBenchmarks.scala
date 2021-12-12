package bench.example

import scala.concurrent.duration._
import bench._
import bench.util._

import scala.collection.mutable
import scala.reflect.ClassTag

object CollectionBenchmarks {
  @inline def normalFlatMap[T, R: ClassTag](arr: Array[T])(f: T => Option[R]): Array[R] = {
    val res = Array.newBuilder[R]
    arr.foreach { t =>
      f(t).foreach(res += _)
    }
    res.result()
  }
  @inline def mapNonNull[T, R <: AnyRef: ClassTag](arr: Array[T])(f: T => R): Array[R] = {
    val res = Array.newBuilder[R]
    arr.foreach { t =>
      val r = f(t)
      if (r != null) res += r
    }
    res.result()
  }

  val linearScan = Comparison(
    "Linear Scan",
    Seq(
      Benchmark[mutable.HashSet[Int]](
        "mutable set foreach",
        n => new mutable.HashSet[Int]() ++ Array.fill[Int](n)(rInt),
        { set =>
          var sum: Int = 0
          set.foreach(sum += _)
          sum
        },
      ),
      Benchmark[Set[Int]](
        "immutable set foreach",
        n => Array.fill[Int](n)(rInt).toSet,
        { set =>
          var sum: Int = 0
          set.foreach(sum += _)
          sum
        },
      ),
      Benchmark[List[Int]](
        "list foreach",
        n => List.fill[Int](n)(rInt),
        { (arr) =>
          var sum: Int = 0
          arr.foreach(sum += _)
          sum
        },
      ),
      Benchmark[mutable.ArrayBuffer[Int]](
        "array buffer foreach",
        n => mutable.ArrayBuffer.fill[Int](n)(rInt),
        { (arr) =>
          var sum: Int = 0
          arr.foreach(sum += _)
          sum
        },
      ),
      Benchmark[Array[Int]](
        "array foreach",
        n => Array.fill[Int](n)(rInt),
        { (arr) =>
          var sum: Int = 0
          arr.foreach(sum += _)
          sum
        },
      ),
      Benchmark[Array[Int]](
        "array while",
        n => Array.fill(n)(rInt),
        { (arr) =>
          var sum: Int = 0
          var i        = 0
          while (i < arr.length) {
            sum += arr(i)
            i += 1
          }
          sum
        },
      ),
    ),
  )

  val construct = Comparison(
    "Construct",
    Seq(
      Benchmark[Int](
        "mutable set",
        n => n,
        { n =>
          val set = mutable.HashSet[String]()
          var i   = 0
          while (i < n) {
            set += i.toString
            i += 1
          }
          set
        },
      ),
      Benchmark[Int](
        "immutable set",
        n => n,
        { n =>
          var set = Set[String]()
          var i   = 0
          while (i < n) {
            set += i.toString
            i += 1
          }
          set
        },
      ),
      Benchmark[Int](
        "list prepend",
        n => n,
        { n =>
          var arr = List.empty[String]
          var i   = 0
          while (i < n) {
            arr ::= i.toString
            i += 1
          }
          arr
        },
      ),
      Benchmark[Int](
        "array buffer",
        n => n,
        { n =>
          val arr = mutable.ArrayBuffer[String]()
          var i   = 0
          while (i < n) {
            arr += i.toString
            i += 1
          }
          arr
        },
      ),
      Benchmark[Int](
        "array builder",
        n => n,
        { n =>
          val arr = Array.newBuilder[String]
          var i   = 0
          while (i < n) {
            arr += i.toString
            i += 1
          }
          arr.result()
        },
      ),
      Benchmark[Int](
        "array",
        n => n,
        { n =>
          val arr = new Array[String](n)
          var i   = 0
          while (i < n) {
            arr(i) = i.toString
            i += 1
          }
          arr
        },
      ),
    ),
  )

  val map = Comparison(
    "Map",
    Seq(
      Benchmark[mutable.HashSet[Int]](
        "mutable set",
        n => new mutable.HashSet[Int]() ++ Array.fill[Int](n)(rInt),
        { set =>
          set.map(_ * 10)
        },
      ),
      Benchmark[Set[Int]](
        "immutable set",
        n => Array.fill[Int](n)(rInt).toSet,
        { set =>
          set.map(_ * 10)
        },
      ),
      Benchmark[List[Int]](
        "list",
        n => List.fill[Int](n)(rInt),
        { (arr) =>
          arr.map(_ * 10)
        },
      ),
      Benchmark[mutable.ArrayBuffer[Int]](
        "array buffer",
        n => mutable.ArrayBuffer.fill[Int](n)(rInt),
        { (arr) =>
          arr.map(_ * 10)
        },
      ),
      Benchmark[Array[Int]](
        "array",
        n => Array.fill[Int](n)(rInt),
        { (arr) =>
          arr.map(_ * 10)
        },
      ),
      Benchmark[Array[Int]](
        "array while",
        n => Array.fill(n)(rInt),
        { (arr) =>
          val arr2 = new Array[Int](arr.length)
          var i    = 0
          while (i < arr.length) {
            arr2(i) += arr(i) * 10
            i += 1
          }
          arr
        },
      ),
    ),
  )

  val flatMap = Comparison(
    "FlatMap",
    Seq(
      Benchmark[Array[String]](
        "flatMap option",
        n => Array.fill[String](n)(rInt.toString),
        { (arr) =>
          arr.flatMap(x => if (x.isEmpty) None else Some(x + 10))
        },
      ),
      Benchmark[Array[String]](
        "flatMap array",
        n => Array.fill[String](n)(rInt.toString),
        { (arr) =>
          arr.flatMap(x => if (x.isEmpty) Array.empty[String] else Array(x + 10))
        },
      ),
      Benchmark[Array[String]](
        "flatMap custom",
        n => Array.fill[String](n)(rInt.toString),
        { (arr) =>
          normalFlatMap(arr)(x => if (x.isEmpty) None else Some(x + 10))
        },
      ),
      Benchmark[Array[String]](
        "mapNonNull",
        n => Array.fill[String](n)(rInt.toString),
        { (arr) =>
          mapNonNull[String, String](arr)(x => if (x.isEmpty) null else x + 10)
        },
      ),
    ),
  )

  val lookup = Comparison(
    "Lookup",
    Seq(
      Benchmark[(Int, mutable.HashSet[Int])](
        "mutable set",
        n => (n, new mutable.HashSet[Int]() ++ Array.range(0, n)),
        { case (n, set) =>
          set.contains(n)
        },
      ),
      Benchmark[(Int, Set[Int])](
        "immutable set",
        n => (n, Array.range(0, n).toSet),
        { case (n, set) =>
          set.contains(n)
        },
      ),
      Benchmark[(Int, mutable.HashSet[Int])](
        "mutable set apply",
        n => (n, new mutable.HashSet[Int]() ++ Array.range(0, n)),
        { case (n, set) =>
          set.apply(n)
        },
      ),
      Benchmark[(Int, Set[Int])](
        "immutable set apply",
        n => (n, Array.range(0, n).toSet),
        { case (n, set) =>
          set.apply(n)
        },
      ),
      // Benchmark[(Int, List[Int])](
      //   "list contains",
      //   n => (n, List.range(0, n)),
      //   { case (n, arr) =>
      //     arr.contains(n)
      //   }
      // ),
      Benchmark[(Int, List[Int])](
        "list exists",
        n => (n, List.range(0, n)),
        { case (n, arr) =>
          arr.exists(_ == n)
        },
      ),
      // Benchmark[(Int, mutable.ArrayBuffer[Int])](
      //   "array buffer contains",
      //   n => (n, mutable.ArrayBuffer.range(0, n)),
      //   { case (n, arr) =>
      //     arr.contains(n)
      //   }
      // ),
      Benchmark[(Int, mutable.ArrayBuffer[Int])](
        "array buffer exists",
        n => (n, mutable.ArrayBuffer.range(0, n)),
        { case (n, arr) =>
          arr.exists(_ == n)
        },
      ),
      // Benchmark[(Int, Array[Int])](
      //   "array contains",
      //   n => (n, Array.range(0, n)),
      //   { case (n, arr) =>
      //     arr.contains(n)
      //   }
      // ),
      Benchmark[(Int, Array[Int])](
        "array exists",
        n => (n, Array.range(0, n)),
        { case (n, arr) =>
          arr.exists(_ == n)
        },
      ),
      Benchmark[(Int, Array[Int])](
        "array while",
        n => (n, Array.range(0, n)),
        { case (n, arr) =>
          var i     = 0
          var found = false
          while (!found && i < arr.length) {
            if (arr(i) == n) found = true
            i += 1
          }
          found
        },
      ),
    ),
  )
}
