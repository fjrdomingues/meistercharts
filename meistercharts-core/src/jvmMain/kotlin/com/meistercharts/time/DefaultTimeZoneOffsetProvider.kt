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
package com.meistercharts.time

import it.neckar.open.time.TimeZone
import java.time.Instant
import java.time.ZoneId

/**
 * Computes the 'real' time-zone offset for a given timestamp and a given time-zone
 */
actual class DefaultTimeZoneOffsetProvider : TimeZoneOffsetProvider {
  override fun timeZoneOffset(timestamp: Double, timeZone: TimeZone): Double {
    val zoneId = ZoneId.of(timeZone.zoneId)
    val instant = Instant.ofEpochMilli(timestamp.toLong())
    val zoneOffset = zoneId.rules.getOffset(instant)
    return zoneOffset.totalSeconds * 1000.0
  }

}
