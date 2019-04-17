package bench

import scala.concurrent.duration._
import util._

case class Comparison(name: String, benchmarks: Seq[BenchmarkLike[_]])

trait BenchmarkLike[T] {
  def name: String
  def runFor(dataSize: Int, minDuration: Duration): (Duration, Long)
}

case class BenchmarkWithoutInit(name: String, code: (Int) => Any) extends BenchmarkLike[Unit] {
  def runFor(dataSize: Int, minDuration: Duration): (Duration, Long) = {
    val (codeDuration, count) = repeatCodeFor(minDuration) { code(dataSize) }
    val avgCodeDuration: Duration = codeDuration / count
    (avgCodeDuration, count)
  }
}

case class BenchmarkImmutableInit[T](name: String, init: Int => T, code: (T) => Any) extends BenchmarkLike[T] {
  def runFor(dataSize: Int, minDuration: Duration): (Duration, Long) = {
    val initData = init(dataSize)
    val (codeDuration, count) = repeatCodeFor(minDuration) { code(initData) }
    val avgCode: Duration = codeDuration / count
    (avgCode, count)
  }
}

// Useful for when the data returned from `init` will be mutated by `code`
case class Benchmark[T](name: String, init: Int => T, code: (T) => Any) extends BenchmarkLike[T] {
  def runFor(dataSize: Int, minDuration: Duration): (Duration, Long) = {
    val (onlyInitDuration, onlyInitCount) = repeatCodeFor(minDuration / 2) {
      init(dataSize)
    }
    val avgOnlyInit: Duration = onlyInitDuration / onlyInitCount

    val (initAndCodeDuration, initAndCodeCount) = repeatCodeFor(minDuration / 2) {
      code(init(dataSize))
    }
    val avgInitAndCode: Duration = initAndCodeDuration / initAndCodeCount
    val avgOnlyCode = avgInitAndCode - avgOnlyInit
    // println(s"avgOnlyInit:    $avgOnlyInit")
    // println(s"avgInitAndCode: $avgInitAndCode")
    // println(s"avgOnlyCode:    $avgOnlyCode")
    (avgOnlyCode, initAndCodeCount)
  }
}
