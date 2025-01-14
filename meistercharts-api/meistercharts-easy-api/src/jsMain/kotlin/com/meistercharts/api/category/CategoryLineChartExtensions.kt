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
package com.meistercharts.api.category

import com.meistercharts.model.category.SeriesIndex
import com.meistercharts.api.applyCategoryAxisStyle
import com.meistercharts.api.applyCrossWireStyle
import com.meistercharts.api.applyLinesStyle
import com.meistercharts.api.applyThresholdStyles
import com.meistercharts.api.applyTitleStyle
import com.meistercharts.api.applyValueAxisStyle
import com.meistercharts.api.line.LineChartLineStyle
import com.meistercharts.api.line.LineChartSimpleConverter
import com.meistercharts.api.toColor
import com.meistercharts.api.toFontDescriptorFragment
import com.meistercharts.api.toModel
import com.meistercharts.api.toModelSizes
import com.meistercharts.api.toNumberFormat
import com.meistercharts.api.toThresholdLabelsProvider
import com.meistercharts.api.toThresholdValuesProvider
import com.meistercharts.api.withValues
import com.meistercharts.charts.CategoryLineChartGestalt
import it.neckar.open.kotlin.lang.asProvider
import it.neckar.open.provider.MultiProvider
import it.neckar.open.unit.other.px
import com.meistercharts.api.line.LineChartSimpleStyle
import it.neckar.logging.Logger
import it.neckar.logging.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.meistercharts.api.category.CategoryLineChartExtensions")


/**
 * Is called initially and applies the Easy API defaults
 */
fun CategoryLineChartGestalt.applyEasyApiDefaults() {
  // use all the available space on the right
  contentViewportMargin = contentViewportMargin.copy(right = 0.0)
  configuration.applyValueAxisTitleOnTop(40.0)
}

/**
 * Applies the style for the line chart simple
 */
fun CategoryLineChartGestalt.applyStyle(jsStyle: LineChartSimpleStyle) {
  logger.debug("CategoryLineChartGestalt.applyStyle", jsStyle)

  //call apply-functions first
  jsStyle.valueRange?.toModel()?.let {
    if (this.configuration.valueRange != it) {
      this.applyValueRange(it)
    }
  }

  jsStyle.valueFormat?.toNumberFormat()?.let {
    this.configuration.numberFormat = it
  }

  jsStyle.minDataPointDistance?.let {
    this.configuration.minCategorySize = it
  }

  jsStyle.maxDataPointDistance?.let {
    this.configuration.maxCategorySize = it
  }

  jsStyle.valueAxisStyle?.let { jsValueAxisStyle ->
    this.valueAxisTopTitleLayer.configuration.applyTitleStyle(jsValueAxisStyle)
    this.valueAxisLayer.axisConfiguration.applyValueAxisStyle(jsValueAxisStyle)

    jsValueAxisStyle.axisSize?.let {
      contentViewportMargin = contentViewportMargin.withSide(valueAxisLayer.axisConfiguration.side, it)
    }
  }

  this.categoryAxisLayer.axisConfiguration.applyCategoryAxisStyle(jsStyle.categoryAxisStyle)
  jsStyle.categoryAxisStyle?.axisSize?.let {
    this.contentViewportMargin = this.contentViewportMargin.withSide(categoryAxisLayer.axisConfiguration.side, it)
  }

  //Thresholds
  this.thresholdsSupport.applyThresholdStyles(jsStyle.thresholds, Unit)
  this.configuration.thresholdValues = jsStyle.thresholds.toThresholdValuesProvider()
  this.configuration.thresholdLabels = jsStyle.thresholds.toThresholdLabelsProvider()

  this.valuesGridLayer.configuration.applyLinesStyle(jsStyle.valuesGridStyle) { this.configuration.valueRange }
  jsStyle.valuesGridStyle?.visible?.let {
    this.configuration.showValuesGrid = it
  }

  this.categoriesGridLayer.configuration.applyLinesStyle(jsStyle.categoriesGridStyle)
  jsStyle.categoriesGridStyle?.visible?.let {
    this.configuration.showCategoriesGrid = it
  }

  jsStyle.lineStyles?.let { jsLineStyles: Array<LineChartLineStyle?> ->
    this.categoryLinesLayer.configuration.pointPainters = LineChartSimpleConverter.toPointPainters(jsLineStyles)
    this.categoryLinesLayer.configuration.linePainters = LineChartSimpleConverter.toLinePainters(jsLineStyles)
    this.categoryLinesLayer.configuration.lineStyles = LineChartSimpleConverter.toLineStyles(jsLineStyles)

    LineChartSimpleConverter.toLinePainters(categoryLinesLayer.configuration.linePainters, jsLineStyles).let {
      this.categoryLinesLayer.configuration.linePainters = it
    }
  }

  jsStyle.showTooltip?.let {
    this.configuration.showTooltip = it
  }

  this.crossWireLineLayer.configuration.applyCrossWireStyle(jsStyle.tooltipWireStyle)

  jsStyle.tooltipStyle?.let { jsTooltipStyle ->
    jsTooltipStyle.tooltipFormat?.toNumberFormat()?.let {
      this.configuration.balloonTooltipValueLabelFormat = it
    }

    jsTooltipStyle.tooltipBoxStyle?.toModel()?.let {
      this.balloonTooltipLayer.tooltipPainter.configuration.boxStyle = it
    }

    jsTooltipStyle.tooltipBoxStyle?.color?.toColor()?.let {
      this.balloonTooltipSupport.tooltipContentPaintable.delegate.configuration.labelColors = MultiProvider.always(it)
      this.balloonTooltipSupport.tooltipContentPaintable.headlinePaintable.configuration.labelColor = it.asProvider()
    }

    jsTooltipStyle.labelWidth?.let {
      this.balloonTooltipSupport.tooltipContentPaintable.delegate.configuration.maxLabelWidth = it
    }

    jsTooltipStyle.symbolSizes?.toModelSizes()?.let {
      this.configuration.applyBalloonTooltipSymbolSize(it.first())
    }

    jsTooltipStyle.symbolLabelGap?.let {
      this.balloonTooltipSupport.tooltipContentPaintable.delegate.configuration.symbolLabelGap = it
    }

    jsTooltipStyle.entriesGap?.let {
      this.balloonTooltipSupport.tooltipContentPaintable.delegate.configuration.entriesGap = it
    }

    jsTooltipStyle.tooltipFont?.toFontDescriptorFragment()?.let { it ->
      this.balloonTooltipSupport.tooltipContentPaintable.delegate.configuration.textFont = it.asProvider()
    }

    jsTooltipStyle.headlineFont?.toFontDescriptorFragment()?.let { it ->
      this.balloonTooltipSupport.tooltipContentPaintable.headlinePaintable.configuration.font = it
    }

    jsTooltipStyle.headlineMarginBottom?.let { it ->
      this.balloonTooltipSupport.tooltipContentPaintable.stackedPaintablesPaintable.configuration.entriesGap = it
    }
  }

  jsStyle.visibleLines?.let { jsVisibleLines ->
    if (jsVisibleLines.size == 1 && jsVisibleLines[0] == -1) {
      this.configuration.lineIsVisible = MultiProvider.always(true)
    } else {
      val visibleLinesSet = jsVisibleLines.toSet()
      this.configuration.lineIsVisible = object : MultiProvider<SeriesIndex, Boolean> {
        override fun valueAt(index: Int): Boolean {
          return visibleLinesSet.contains(index)
        }
      }
    }
  }

  @px val viewportMarginTop = valueAxisSupport.calculateContentViewportMarginTop(Unit, chartSupport())
  contentViewportMargin = contentViewportMargin.withTop(viewportMarginTop)

  //Apply the content viewport margin
  jsStyle.contentViewportMargin?.let {
    contentViewportMargin = contentViewportMargin.withValues(it)
  }
}
