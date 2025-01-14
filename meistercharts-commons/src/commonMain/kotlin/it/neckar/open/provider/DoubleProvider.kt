package it.neckar.open.provider

import it.neckar.open.unit.si.ms
import kotlin.reflect.KProperty0

/**
 * Provides a *single* double - to avoid boxing
 */
fun interface DoubleProvider {
  /**
   * Provides the double
   */
  operator fun invoke(): Double

  companion object {
    /**
     * Always returns [Double.NaN]
     */
    val NaN: DoubleProvider = DoubleProvider { Double.NaN }

    /**
     * Always returns [0.0]
     */
    val Zero: DoubleProvider = DoubleProvider { 0.0 }

    /**
     * Well known provider that returns now
     */
    val nowMillis: @ms DoubleProvider = DoubleProvider { it.neckar.open.time.nowMillis() }
  }
}

/**
 * Wraps the given double in a DoubleProvider
 */
fun Double.asDoubleProvider(): DoubleProvider {
  return DoubleProvider { this }
}

/**
 * Returns a delegate that uses the current value of this property to delegate all calls.
 */
fun KProperty0<DoubleProvider>.delegate(): DoubleProvider {
  return DoubleProvider {
    get().invoke()
  }
}
