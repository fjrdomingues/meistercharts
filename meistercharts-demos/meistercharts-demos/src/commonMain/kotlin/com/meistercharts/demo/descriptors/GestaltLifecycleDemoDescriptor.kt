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
package com.meistercharts.demo.descriptors

import com.meistercharts.algorithms.layers.addClearBackground
import com.meistercharts.algorithms.layers.text.TextLayer
import com.meistercharts.charts.AbstractChartGestalt
import com.meistercharts.demo.ChartingDemo
import com.meistercharts.demo.ChartingDemoDescriptor
import com.meistercharts.demo.DemoCategory
import com.meistercharts.demo.PredefinedConfiguration

/**
 */
class GestaltLifecycleDemoDescriptor : ChartingDemoDescriptor<Nothing> {
  override val name: String = "Gestalt Life cycle"

  override val category: DemoCategory = DemoCategory.Calculations

  override fun createDemo(configuration: PredefinedConfiguration<Nothing>?): ChartingDemo {
    return ChartingDemo {
      meistercharts {
        MyDemoGestalt().configure(this)
      }
    }
  }

  class MyDemoGestalt : AbstractChartGestalt() {
    init {
      configure {
        layers.addClearBackground()

        layers.addLayer(TextLayer({ textService, i18nConfiguration ->
          val gestalt = this@MyDemoGestalt
          val gestaltChartSupport = gestalt.chartSupport()

          listOf("Gestalt Chart Support: $gestaltChartSupport")
        }))
      }
    }
  }
}
