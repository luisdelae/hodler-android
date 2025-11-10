package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.remote.dto.MarketChartDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketChartMapperTest {

    @Test
    fun `MarketChartDto toMarketChart maps price points correctly`() {
        // Arrange
        val dto = MarketChartDto(
            prices = listOf(
                listOf(1699920000000.0, 54000.0),
                listOf(1699923600000.0, 54500.0),
                listOf(1699927200000.0, 54200.0)
            )
        )

        // Act
        val domain = dto.toMarketChart()

        // Assert
        assertEquals(3, domain.prices.size)
        assertEquals(1699920000000L, domain.prices[0].timestamp)
        assertEquals(54000.0, domain.prices[0].price, 0.01)
        assertEquals(1699923600000L, domain.prices[1].timestamp)
        assertEquals(54500.0, domain.prices[1].price, 0.01)
        assertEquals(1699927200000L, domain.prices[2].timestamp)
        assertEquals(54200.0, domain.prices[2].price, 0.01)
    }

    @Test
    fun `MarketChartDto toMarketChart handles empty price list`() {
        // Arrange
        val dto = MarketChartDto(prices = emptyList())

        // Act
        val domain = dto.toMarketChart()

        // Assert
        assertTrue(domain.prices.isEmpty())
    }

    @Test
    fun `MarketChartDto toMarketChart preserves chronological order`() {
        // Arrange - Prices in chronological order
        val dto = MarketChartDto(
            prices = listOf(
                listOf(1699920000000.0, 54000.0),
                listOf(1699923600000.0, 54500.0),
                listOf(1699927200000.0, 55000.0),
                listOf(1699930800000.0, 54800.0)
            )
        )

        // Act
        val domain = dto.toMarketChart()

        // Assert - Timestamps should be increasing
        for (i in 0 until domain.prices.size - 1) {
            assertTrue(domain.prices[i].timestamp < domain.prices[i + 1].timestamp)
        }
    }
}
