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
package com.meistercharts.canvas.animation

import assertk.*
import assertk.assertions.*
import com.meistercharts.canvas.ChartSupport
import com.meistercharts.canvas.DefaultLayerSupport
import com.meistercharts.canvas.layer.LayerSupport
import com.meistercharts.canvas.MockCanvas
import it.neckar.open.observable.ObservableDouble
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChartAnimationTest {
  lateinit var canvas: MockCanvas
  lateinit var chartSupport: ChartSupport
  lateinit var layerSupport: LayerSupport

  @BeforeEach
  internal fun setUp() {
    canvas = MockCanvas()
    chartSupport = ChartSupport(canvas)
    layerSupport = DefaultLayerSupport(chartSupport)
  }

  @Test
  fun testFinished() {
    val chartAnimation = ChartAnimation { frameTimestamp ->
      AnimationState.finishedIf(frameTimestamp > 10_000.0)
    }

    assertThat(chartSupport.renderLoopListeners).containsNone(chartAnimation)
    chartSupport.onRender(chartAnimation)
    assertThat(chartSupport.renderLoopListeners).contains(chartAnimation)

    //Simulate an event
    chartSupport.render(10_000.0, 100.0)
    assertThat(chartSupport.renderLoopListeners).contains(chartAnimation)

    chartSupport.render(20_000.0, 200.0)
    assertThat(chartSupport.renderLoopListenersToRemove).contains(chartAnimation)
    assertThat(chartSupport.renderLoopListeners).contains(chartAnimation)

    chartSupport.render(30_000.0, 300.0)
    assertThat(chartSupport.renderLoopListeners).containsNone(chartAnimation)
  }

  @Test
  fun testDispose() {
    val chartAnimation = ChartAnimation { frameTimestamp ->
      AnimationState.finishedIf(frameTimestamp > 10_000.0)
    }

    assertThat(chartSupport.renderLoopListeners).containsNone(chartAnimation)
    assertThat(chartSupport.renderLoopListeners).hasSize(1)
    chartSupport.onRender(chartAnimation)
    assertThat(chartSupport.renderLoopListeners).hasSize(2)
    assertThat(chartSupport.renderLoopListeners).contains(chartAnimation)

    chartSupport.render(10_000.0, 100.0)
    assertThat(chartSupport.renderLoopListeners).hasSize(2)
    assertThat(chartSupport.renderLoopListeners).contains(chartAnimation)

    chartAnimation.dispose()

    assertThat(chartSupport.renderLoopListeners).hasSize(2)
    assertThat(chartSupport.renderLoopListeners).contains(chartAnimation)

    chartSupport.render(20_000.0, 200.0)

    assertThat(chartSupport.renderLoopListenersToRemove).contains(chartAnimation)
    chartSupport.render(30_000.0, 300.0)

    assertThat(chartSupport.renderLoopListeners).hasSize(1)
    assertThat(chartSupport.renderLoopListeners).containsNone(chartAnimation)
  }

  @Test
  fun testUsageWithProperty() {
    val positionY = ObservableDouble(0.0)

    val tweenDefinition = TweenDefinition(1000.0)
    val tween = tweenDefinition.realize(40_000.0)

    val propertyTween = tween.animate(positionY::value, 7.0)

    propertyTween.update(40_000.0)
    assertThat(positionY.value).isEqualTo(0.0)

    propertyTween.update(41_000.0)
    assertThat(positionY.value).isEqualTo(7.0)

    propertyTween.update(40_500.0)
    assertThat(positionY.value).isEqualTo(3.5)
  }
}
