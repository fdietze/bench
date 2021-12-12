package bench

import scala.concurrent.duration._
import util._

case class Comparison(name: String, benchmarks: Seq[BenchmarkLike[_]])

trait BenchmarkLike[T] {
  def name: String
  def runFor(dataSize: Int, minDuration: Duration): Duration
  protected def countWarning = 10
}

case class BenchmarkWithoutInit(name: String, code: (Int) => Any) extends BenchmarkLike[Unit] {
  def runFor(dataSize: Int, minDuration: Duration): Duration = {
    val (codeDuration, count)     = repeatCodeFor(minDuration) { code(dataSize) }
    if (count <= countWarning) println(s"WARNING: only ran $count times for size $dataSize. Give me more time.")
    val avgCodeDuration: Duration = codeDuration / count
    avgCodeDuration
  }
}

case class BenchmarkImmutableInit[T](name: String, init: Int => T, code: (T) => Any) extends BenchmarkLike[T] {
  def runFor(dataSize: Int, minDuration: Duration): Duration = {
    val initData              = init(dataSize)
    val (codeDuration, count) = repeatCodeFor(minDuration) { code(initData) }
    if (count <= countWarning) println(s"WARNING: only ran $count times for size $dataSize. Give me more time.")
    val avgCode: Duration     = codeDuration / count
    avgCode
  }
}

// Useful for when the data returned from `init` will be mutated by `code`
case class Benchmark[T](name: String, init: Int => T, code: (T) => Any) extends BenchmarkLike[T] {
  def runFor(dataSize: Int, minDuration: Duration): Duration = {
    val (onlyInitDuration, onlyInitCount) = repeatCodeFor(minDuration / 2) {
      init(dataSize)
    }
    val avgOnlyInit: Duration             = onlyInitDuration / onlyInitCount

    val (initAndCodeDuration, initAndCodeCount) = repeatCodeFor(minDuration / 2) {
      code(init(dataSize))
    }
    if (initAndCodeCount <= countWarning)
      println(s"WARNING: only ran $initAndCodeCount times for size $dataSize. Give me more time.")

    val avgInitAndCode: Duration = initAndCodeDuration / initAndCodeCount
    val avgOnlyCode              = avgInitAndCode - avgOnlyInit
    // println(s"avgOnlyInit:    $avgOnlyInit")
    // println(s"avgInitAndCode: $avgInitAndCode")
    // println(s"avgOnlyCode:    $avgOnlyCode")
    avgOnlyCode
  }
}
