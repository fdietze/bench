package bench

import scala.concurrent.duration._
import util._

sealed trait BenchmarkLike[T] {
  def name: String
  def init: Int => T // TODO: remove init from trait
  def runWithInit(size: Int, iterations: Long): Unit
}

case class BenchmarkWithoutInit(name: String, code: (Int) => Any) extends BenchmarkLike[Unit] {
  val init: Int => Unit = _ => ()
  def runWithInit(size: Int, iterations: Long) = loop(iterations) { init(0); code(size) }
}

case class Benchmark[T](name: String, init: Int => T, code: (T) => Any) extends BenchmarkLike[T] {
  def runWithInit(size: Int, iterations: Long) = loop(iterations) { code(init(size)) }
}

case class Comparison(name: String, benchmarks: Seq[BenchmarkLike[_]])
