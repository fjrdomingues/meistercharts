/**
 * Copyright 2023 Neckar IT GmbH, Mössingen, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meistercharts.history.generator

import com.meistercharts.algorithms.ValueRange
import com.meistercharts.algorithms.coerceIn
import com.meistercharts.animation.Easing
import com.meistercharts.history.MayBeNoValueOrPending
import it.neckar.open.kotlin.lang.cos
import it.neckar.open.kotlin.lang.random
import it.neckar.open.kotlin.lang.randomNormal
import it.neckar.open.kotlin.lang.sin
import it.neckar.open.provider.DoubleProvider
import it.neckar.open.time.nowMillis
import it.neckar.open.unit.number.MayBeNaN
import it.neckar.open.unit.other.pct
import it.neckar.open.unit.si.ms
import it.neckar.open.unit.si.rad
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates values.
 * Very useful for sample/demo data
 */
fun interface DecimalValueGenerator {
  /**
   * Generates a value for the input [timestamp]
   */
  fun generate(timestamp: @ms Double): @MayBeNaN Double

  companion object {
    /**
     * Returns the constant value
     */
    fun always(always: Double): ConstantDecimalValueGenerator {
      return ConstantDecimalValueGenerator(always)
    }

    /**
     * Returns a [RandomDecimalValueGenerator] that generates random decimal values within the specified [valueRange].
     *
     * @param valueRange the range of values that can be generated by the [RandomDecimalValueGenerator].
     * @param randomGenerator the [Random] object used to generate the random decimal values. Defaults to [random].
     * @return a [RandomDecimalValueGenerator] that generates random decimal values within the specified [valueRange].
     */
    fun random(
      valueRange: ValueRange,
      randomGenerator: Random = random,
    ): RandomDecimalValueGenerator {
      return RandomDecimalValueGenerator(valueRange, randomGenerator)
    }

    /**
     * Returns a [RandomNormalDecimalValueGenerator] that generates normally distributed random decimal values within the specified [valueRange].
     *
     * The distribution is centered around the midpoint of the value range and has a standard deviation determined by the [sigmaAbsolute] parameter.
     *
     * @param valueRange the range of values that can be generated by the [RandomNormalDecimalValueGenerator].
     * @param sigmaAbsolute the standard deviation of the normal distribution, expressed as an absolute value. Default value is 2% of the value range delta.
     * @param center the center of the normal distribution. Default value is the midpoint of the value range.
     * @return a [RandomNormalDecimalValueGenerator] that generates normally distributed random decimal values within the specified [valueRange].
     */
    fun normality(
      valueRange: ValueRange,
      /**
       * The sigma - by default 2% of the value range delta
       */
      sigmaAbsolute: Double = valueRange.delta * 0.02,
      center: Double = valueRange.center(),
    ): RandomNormalDecimalValueGenerator {
      return RandomNormalDecimalValueGenerator(valueRange, sigmaAbsolute, center)
    }

    /**
     * Returns a [SineDecimalValueGenerator] that generates decimal values following a sine curve within the specified [valueRange].
     *
     * The generator starts at the minimum value of the value range, and its values follow a sine curve with a frequency that is determined by the [angleIncrement] parameter.
     *
     * @param valueRange the range of values that can be generated by the [SineDecimalValueGenerator].
     * @param angleIncrement the increment in radians used to determine the frequency of the sine curve. Default value is PI/100.
     * @return a [SineDecimalValueGenerator] that generates decimal values following a sine curve within the specified [valueRange].
     */
    fun sine(
      valueRange: ValueRange,
      angleIncrement: @rad Double = PI / 100.0,
    ): SineDecimalValueGenerator {
      return SineDecimalValueGenerator(valueRange, angleIncrement)
    }

    /**
     * Returns a [CosineDecimalValueGenerator] that generates decimal values following a cosine curve within the specified [valueRange].
     *
     * The generator starts at the minimum value of the value range, and its values follow a cosine curve with a frequency that is determined by the [angleIncrement] parameter.
     *
     * @param valueRange the range of values that can be generated by the [CosineDecimalValueGenerator].
     * @param angleIncrement the increment in radians used to determine the frequency of the cosine curve. Default value is PI/100.
     * @return a [CosineDecimalValueGenerator] that generates decimal values following a cosine curve within the specified [valueRange].
     */
    fun cosine(
      valueRange: ValueRange,
      angleIncrement: @rad Double = PI / 100.0,
    ): CosineDecimalValueGenerator {
      return CosineDecimalValueGenerator(valueRange, angleIncrement)
    }
  }
}

/**
 * Generates a single value
 */
class ConstantDecimalValueGenerator(val always: Double) : DecimalValueGenerator {
  override fun generate(timestamp: Double): Double {
    return always
  }
}

/**
 * Generates random decimal values within the specified [valueRange] using the provided [randomGenerator].
 *
 * @param valueRange the range of values that can be generated by the [RandomDecimalValueGenerator].
 * @param randomGenerator the [Random] object used to generate the random decimal values. Defaults to [random].
 */
class RandomDecimalValueGenerator(
  val valueRange: ValueRange,
  val randomGenerator: Random = random,
) : DecimalValueGenerator {


  override fun generate(timestamp: Double): Double {
    return randomGenerator.nextDouble(valueRange.start, valueRange.end)
  }
}

/**
 * Generates normally distributed random decimal values within the specified [valueRange] using the provided [sigma].
 *
 * @param valueRange the range of values that can be generated by the [RandomNormalDecimalValueGenerator].
 * @param sigma the standard deviation of the normal distribution used to generate the values.
 * @param center the center of the normal distribution. Default value is the midpoint of the value range.
 */
class RandomNormalDecimalValueGenerator(
  val valueRange: ValueRange,
  val sigma: Double,
  val center: Double = valueRange.center(),
) : DecimalValueGenerator {
  /**
   * Generates a normally distributed random decimal value within the specified [valueRange] using the provided [sigma].
   *
   * @param timestamp the timestamp associated with the generated value.
   * @return a normally distributed random decimal value within the specified [valueRange] using the provided [sigma].
   */
  override fun generate(timestamp: Double): Double {
    return randomNormal(center, sigma).coerceIn(valueRange)
  }
}

/**
 *
 */
class SmoothDecimalValueGenerator(
  private val amplitude: Double = 0.5,
  private val frequency: Double = 0.0001005,

  private val seed: Int = 42,

  ) : DecimalValueGenerator {

  override fun generate(timestamp: @ms Double): Double {
    val random = Random(seed + timestamp.toLong())

    val sinValue = sin(frequency * timestamp)
    val randomValue = random.nextDouble(-amplitude, amplitude)
    val currentValue = 0.5 + sinValue * randomValue

    return currentValue.coerceIn(0.0, 1.0)
  }
}

/**
 * Generates decimal values following a sine curve within the specified [valueRange] using the provided [angleIncrement].
 *
 * The generator starts at the minimum value of the value range, and its values follow a sine curve with a frequency that is determined by the [angleIncrement] parameter.
 *
 * @param valueRange the range of values that can be generated by the [SineDecimalValueGenerator].
 * @param angleIncrement the increment in radians used to determine the frequency of the sine curve. Default value is PI/100.
 */
class SineDecimalValueGenerator(
  val valueRange: ValueRange,
  private val angleIncrement: @rad Double = PI / 100.0,
) : DecimalValueGenerator {
  private val center: Double = (valueRange.start + valueRange.end) / 2.0
  private var angle: @rad Double = 0.0


  /**
   * Generates a decimal value following a sine curve within the specified [valueRange] using the provided [angleIncrement].
   *
   * The generator starts at the minimum value of the value range, and its values follow a sine curve with a frequency that is determined by the [angleIncrement] parameter.
   *
   * @param timestamp the timestamp associated with the generated value.
   * @return a decimal value following a sine curve within the specified [valueRange] using the provided [angleIncrement].
   */
  override fun generate(timestamp: Double): Double {
    angle += angleIncrement
    if (angle >= 2 * PI) {
      angle -= 2 * PI
    }
    return center + angle.sin() * valueRange.delta * 0.5
  }
}

/**
 * Generates decimal values following a cosine curve within the specified [valueRange] using the provided [angleIncrement].
 *
 * The generator starts at the minimum value of the value range, and its values follow a cosine curve with a frequency that is determined by the [angleIncrement] parameter.
 *
 * @property valueRange the range of values that can be generated by the [CosineDecimalValueGenerator].
 * @property angleIncrement the increment in radians used to determine the frequency of the cosine curve. Default value is PI/100.
 */
class CosineDecimalValueGenerator(val valueRange: ValueRange, private val angleIncrement: @rad Double = PI / 100.0) : DecimalValueGenerator {
  private val center: Double = (valueRange.start + valueRange.end) / 2.0
  private var angle: @rad Double = 0.0
  override fun generate(timestamp: Double): Double {
    angle += angleIncrement
    if (angle >= 2 * PI) {
      angle -= 2 * PI
    }
    return center + angle.cos() * valueRange.delta * 0.5
  }
}

/**
 * Scales the output of the provided [decimalValueGenerator] by a given [scale] factor and adds an optional [offset] value.
 *
 * @property decimalValueGenerator the [DecimalValueGenerator] whose output should be scaled.
 * @property scale the scaling factor to be applied to the output of the [decimalValueGenerator].
 * @property offset the offset to be added to the scaled output. Defaults to 0.0.
 */
class ScalingDecimalValueGenerator(
  val decimalValueGenerator: DecimalValueGenerator,
  val scale: Double,
  val offset: Double = 0.0,
) : DecimalValueGenerator {
  override fun generate(timestamp: Double): Double {
    return offset + decimalValueGenerator.generate(timestamp) * scale
  }
}

/**
 * Generates a repeating value sequence
 * @param easing the easing to be used
 * @param period the time period of the sequence; the sequence repeats itself after this time period
 * @param shift a time based offset into the sequence
 * @param center the smallest possible value of the sequence is [center] - [deviation] while the largest possible value is [center] + [deviation]
 * @param deviation the smallest possible value of the sequence is [center] - [deviation] while the largest possible value is [center] + [deviation]
 */
fun repeatingValues(
  easing: Easing,
  period: @ms Double,
  shift: @ms Double = 0.0,
  center: @pct Double = 0.5,
  deviation: @pct Double = 1.0 - center * 0.5
): @pct Double {
  //the sine ensures that the values are repeated
  val sine = ((nowMillis() - shift) / period * 2 * PI).sin()
  //sine is in [-1.0, 1.0] but easing needs a value in [0.0, 1.0]
  @pct val sineAdjusted = (sine + 1.0) * 0.5
  //ensure that eased value lies around center
  val result = center - 0.5 * easing(sineAdjusted) * deviation
  check(result <= center + deviation) { "result <$result> should be less than or equal to ${center + deviation}" }
  check(result >= center - deviation) { "result <$result> should be greater than or equal to ${center - deviation}" }
  return result
}

/**
 * Creates a provider that passes the current time to this [DecimalValueGenerator] every time it is invoked
 */
fun DecimalValueGenerator.forNow(): DoubleProvider {
  return DoubleProvider {
    generate(nowMillis())
  }
}

/**
 * Scales the values generated by this and adds an offset.
 *
 * @param scale the factor by which all values are multiplied
 * @param offset the value to be added to all generated values
 */
fun DecimalValueGenerator.scaled(
  scale: Double,
  offset: Double = 0.0,
): ScalingDecimalValueGenerator {
  return ScalingDecimalValueGenerator(this, scale, offset)
}

/**
 * Offsets the values generated by this
 */
fun DecimalValueGenerator.offset(
  offset: Double = 0.0,
): ScalingDecimalValueGenerator {
  return ScalingDecimalValueGenerator(this, 1.0, offset)
}
