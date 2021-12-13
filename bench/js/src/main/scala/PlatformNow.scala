package bench.util
import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.annotation._

@js.native
@JSImport("perf_hooks", "performance")
object NodejsPerformance extends js.Object {
  def now(): Double = js.native
}

package object PlatformNow {
  // This is a workaround for:
  // https://github.com/scala-js/scala-js/issues/3623

  private[this] val getHighPrecisionTime: js.Function0[scala.Double] = {
    // From https://github.com/scala-js/scala-js/blob/master/javalanglib/src/main/scala/java/lang/System.scala#L42
    import js.DynamicImplicits.truthValue

    if (js.typeOf(global.performance) != "undefined") {
      if (global.performance.now) { () =>
        global.performance.now().asInstanceOf[scala.Double]
      } else if (global.performance.webkitNow) { () =>
        global.performance.webkitNow().asInstanceOf[scala.Double]
      } else { () =>
        new js.Date().getTime()
      }
    } else if (NodejsPerformance.asInstanceOf[js.UndefOr[NodejsPerformance.type]].isDefined) { () =>
      NodejsPerformance.now()

    } else { () =>
      new js.Date().getTime()
    }
  }

  @inline def nanoTime: Long = (getHighPrecisionTime() * 1000000).toLong
}
