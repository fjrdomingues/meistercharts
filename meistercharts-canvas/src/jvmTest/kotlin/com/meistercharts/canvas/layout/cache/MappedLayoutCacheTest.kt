package com.meistercharts.canvas.layout.cache

import assertk.*
import assertk.assertions.*
import com.meistercharts.canvas.PaintingLoopIndex
import com.meistercharts.history.ReferenceEntryDataSeriesIndex
import it.neckar.open.test.utils.isNaN
import org.junit.jupiter.api.Test

class MappedLayoutCacheTest {
  @Test
  fun testApi() {
    val cache = MappedLayoutCache<ReferenceEntryDataSeriesIndex, MyLayoutVariable> {
      MyLayoutVariable()
    }

    //Clear once at the start
    cache.clear()
    assertThat(cache.values).hasSize(0)

    val index7 = ReferenceEntryDataSeriesIndex(7)
    val index8 = ReferenceEntryDataSeriesIndex(8)

    assertThat(cache.get(index7).xLocation).isNaN()
    assertThat(cache.values).hasSize(1)
    assertThat(cache.get(index8).xLocation).isNaN()
    assertThat(cache.values).hasSize(2)

    cache.get(index7).xLocation = 7.7
    cache.get(index8).xLocation = 8.8

    assertThat(cache.get(index7).xLocation).isEqualTo(7.7)
    assertThat(cache.get(index8).xLocation).isEqualTo(8.8)

    cache.clear()

    assertThat(cache.get(index7).xLocation).isNaN()
    assertThat(cache.get(index8).xLocation).isNaN()
  }

  @Test
  fun testSimulatePaintSameIndices() {
    var callCount = 0

    val cache = MappedLayoutCache<ReferenceEntryDataSeriesIndex, MyLayoutVariable> {
      callCount++
      MyLayoutVariable()
    }

    assertThat(callCount).isEqualTo(0)

    val index7 = ReferenceEntryDataSeriesIndex(7)
    val index8 = ReferenceEntryDataSeriesIndex(8)

    //
    //First paint
    //
    cache.resetIfNewLoopIndex(PaintingLoopIndex(0))

    //simulate layout
    cache.get(index7).xLocation = 7.7
    cache.get(index8).xLocation = 8.8
    assertThat(callCount).isEqualTo(2)

    //simulate paint
    assertThat(cache.get(index7).xLocation).isEqualTo(7.7)
    assertThat(cache.get(index8).xLocation).isEqualTo(8.8)


    //Second repaint
    cache.resetIfNewLoopIndex(PaintingLoopIndex(1))
    assertThat(cache.get(index7).xLocation).isNaN()
    assertThat(cache.get(index8).xLocation).isNaN()

    assertThat(callCount).isEqualTo(2)

    //simulate layout
    cache.get(index7).xLocation = 7.7
    cache.get(index8).xLocation = 8.8
    assertThat(callCount).isEqualTo(2)

    //simulate paint
    assertThat(cache.get(index7).xLocation).isEqualTo(7.7)
    assertThat(cache.get(index8).xLocation).isEqualTo(8.8)
  }
}

class MyLayoutVariable : LayoutVariable {
  var xLocation: Double = Double.NaN

  override fun reset() {
    xLocation = Double.NaN
  }
}
