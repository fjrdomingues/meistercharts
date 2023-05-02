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

import com.meistercharts.algorithms.layers.AbstractLayer
import com.meistercharts.algorithms.layers.LayerPaintingContext
import com.meistercharts.algorithms.layers.LayerType
import com.meistercharts.algorithms.layers.addClearBackground
import com.meistercharts.algorithms.layers.legend.VerticallyStackedLabelsPaintable
import com.meistercharts.algorithms.painter.Color
import com.meistercharts.canvas.ChartSupport
import com.meistercharts.canvas.paintMark
import com.meistercharts.canvas.saved
import com.meistercharts.demo.ChartingDemo
import com.meistercharts.demo.ChartingDemoDescriptor
import com.meistercharts.demo.DemoCategory
import com.meistercharts.demo.PredefinedConfiguration
import com.meistercharts.demo.configurableColor
import com.meistercharts.demo.configurableDouble
import com.meistercharts.demo.configurableFontProvider
import it.neckar.open.provider.SizedProvider1

/**
 * A simple hello world demo.
 *
 * Can be used as template to create new demos
 */
class VerticallyStackedPaintablesDemoDescriptor : ChartingDemoDescriptor<Nothing> {
  override val name: String = "Vertically Stacked Paintables Paintable"
  override val category: DemoCategory = DemoCategory.Paintables

  override fun createDemo(configuration: PredefinedConfiguration<Nothing>?): ChartingDemo {
    return ChartingDemo {
      meistercharts {
        configure {
          layers.addClearBackground()

          val paintable = VerticallyStackedLabelsPaintable(
            labels = object : SizedProvider1<String, ChartSupport> {
              override fun valueAt(index: Int, param1: ChartSupport): String {
                return "Text @ $index"
              }

              override fun size(param1: ChartSupport): Int {
                return 8
              }
            },
          )

          layers.addLayer(object : AbstractLayer() {
            override val type: LayerType = LayerType.Content

            override fun paint(paintingContext: LayerPaintingContext) {
              val gc = paintingContext.gc

              gc.translateToCenter()
              gc.paintMark()

              gc.saved {
                paintable.paint(paintingContext)
              }

              gc.stroke(Color.orange)
              gc.strokeRect(paintable.boundingBox(paintingContext))
            }
          })


          configurableFontProvider("Font", paintable.configuration::textFont)
          configurableColor("Label Color", paintable.configuration::labelColor)
          configurableDouble("Entries Gap", paintable.configuration::entriesGap) {
            max = 50.0
          }
        }
      }
    }
  }
}
