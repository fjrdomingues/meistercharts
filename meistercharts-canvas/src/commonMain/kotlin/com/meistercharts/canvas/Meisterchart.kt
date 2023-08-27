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
package com.meistercharts.canvas

import com.meistercharts.canvas.layer.LayerSupport
import it.neckar.open.dispose.Disposable
import it.neckar.open.dispose.OnDispose

/**
 * The(!) MeisterChart interface
 */
interface Meisterchart : Disposable, OnDispose {
  /**
   * The [ChartSupport] for this chart
   */
  val chartSupport: ChartSupport

  /**
   * Contains a description that helps to understand why this MeisterCharts instance has been created and where it is used.
   * Only for debugging purposes.
   */
  val description: String

  /**
   * Helper method that returns the [LayerSupport] of the chart
   */
  val layerSupport: LayerSupport
    get() {
      return chartSupport.layerSupport
    }

  override fun dispose() {
    chartSupport.dispose()
  }

  override fun onDispose(action: () -> Unit) {
    chartSupport.onDispose(action)
  }

  /**
   * Marks the canvas as dirty
   */
  fun markAsDirty(reason: DirtyReason) {
    chartSupport.markAsDirty(reason)
  }
}
