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
package com.meistercharts.algorithms

import com.meistercharts.algorithms.tile.GlobalTilesCache
import com.meistercharts.charts.ChartId

/**
 * Supports handling of global caches.
 *
 * Every time a chart is disposed, this object is used to clean up all global caches
 */
object GlobalCacheSupport {
  /**
   * Cleans all global caches for the given chart id.
   * This method is *only* called from the ChartSupport.
   */
  fun cleanup(chartId: ChartId) {
    GlobalTilesCache.clear(chartId)
  }
}
