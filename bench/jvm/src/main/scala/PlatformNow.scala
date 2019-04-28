package bench.util

package object PlatformNow {
  @inline def nanoTime = System.nanoTime()
}
