package bench

import scala.concurrent.duration._
import util._

trait BenchmarkLike[T] {
  def name: String
  def init: Int => T
  def run(size: Int, iterations: Long)
}

case class BenchmarkWithoutInit(name: String, code: (Int) => Any) extends BenchmarkLike[Unit] {
  val init: Int => Unit = _ => ()
  def run(size: Int, iterations: Long) = loop(iterations) { code(size) }
}

case class Benchmark[T](name: String, init: Int => T, code: (T) => Any) extends BenchmarkLike[T] {
  def run(size: Int, iterations: Long) = loop(iterations) { code(init(size)) }
}

case class BenchmarkCustomIterations[T](name: String, init: Int => T, code: (T, Long) => Any) extends BenchmarkLike[T] {
  def run(size: Int, iterations: Long) = code(init(size), iterations)
}

case class Comparison(name: String, benchmarks: Seq[BenchmarkLike[_]])
