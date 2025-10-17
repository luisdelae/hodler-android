package com.luisd.hodler.presentation.ui.util

import com.luisd.hodler.domain.model.PricePoint
import com.luisd.hodler.presentation.ui.details.TimeRange
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Mock data generator for testing chart functionality without API calls.
 * Generated with AI assistance to quickly create realistic price patterns
 * for different time ranges during development.
 */
fun getMockPriceData(timeRange: TimeRange): List<PricePoint> {
    val now = System.currentTimeMillis()

    val (startTime, pointCount, interval) = when (timeRange) {
        TimeRange.DAY_1 -> Triple(
            now - (24 * 60 * 60 * 1000L),
            48, // 48 points (30-min intervals)
            30 * 60 * 1000L // 30 minutes
        )

        TimeRange.DAY_7 -> Triple(
            now - (7 * 24 * 60 * 60 * 1000L),
            168, // 168 points (hourly)
            60 * 60 * 1000L // 1 hour
        )

        TimeRange.DAY_30 -> Triple(
            now - (30 * 24 * 60 * 60 * 1000L),
            720, // 720 points (hourly)
            60 * 60 * 1000L // 1 hour
        )

        TimeRange.YEAR_1 -> Triple(
            now - (365 * 24 * 60 * 60 * 1000L),
            365, // 365 points (daily)
            24 * 60 * 60 * 1000L // 1 day
        )
    }

    // Different base prices for variety
    val basePrice = when (timeRange) {
        TimeRange.DAY_1 -> 105000.0   // Current price
        TimeRange.DAY_7 -> 102000.0   // Slightly lower start
        TimeRange.DAY_30 -> 95000.0   // Lower start for bigger range
        TimeRange.YEAR_1 -> 45000.0   // Much lower for yearly view
    }

    return List(pointCount) { index ->
        val timestamp = startTime + (index * interval)
        val progress = index.toDouble() / pointCount

        // Scale volatility based on time range
        val (trendMultiplier, volatility) = when (timeRange) {
            TimeRange.DAY_1 -> 500.0 to 200.0    // Small movement
            TimeRange.DAY_7 -> 3000.0 to 500.0   // Medium movement
            TimeRange.DAY_30 -> 10000.0 to 1500.0 // Larger movement
            TimeRange.YEAR_1 -> 60000.0 to 5000.0 // Huge movement (45k to 105k)
        }

        // Overall trend
        val trend = progress * trendMultiplier

        // Periodic patterns based on time range
        val periodicPattern = when (timeRange) {
            TimeRange.DAY_1 -> {
                // Intraday pattern (U-shape: dip in middle of day)
                sin(progress * PI) * -300
            }

            TimeRange.DAY_7 -> {
                // Weekly pattern (weekend dip)
                val dayOfWeek = (index / 24.0) % 7
                if (dayOfWeek > 5) -1000.0 else 0.0
            }

            TimeRange.DAY_30 -> {
                // Multiple weekly cycles
                sin(progress * 4 * PI) * 2000
            }

            TimeRange.YEAR_1 -> {
                // Quarterly cycles
                sin(progress * 4 * PI) * 10000
            }
        }

        // Random walk
        val randomComponent = (Random.nextDouble() - 0.5) * volatility

        // Add occasional spikes/dips (more frequent in shorter timeframes)
        val eventChance = when (timeRange) {
            TimeRange.DAY_1 -> 0.1    // 10% chance per point
            TimeRange.DAY_7 -> 0.05   // 5% chance
            TimeRange.DAY_30 -> 0.02  // 2% chance
            TimeRange.YEAR_1 -> 0.01  // 1% chance
        }
        val eventSpike = if (Random.nextDouble() < eventChance) {
            (Random.nextDouble() - 0.5) * volatility * 3
        } else 0.0

        val price = basePrice + trend + periodicPattern + randomComponent + eventSpike

        // Set bounds based on time range
        val (minPrice, maxPrice) = when (timeRange) {
            TimeRange.DAY_1 -> 104000.0 to 106000.0
            TimeRange.DAY_7 -> 100000.0 to 108000.0
            TimeRange.DAY_30 -> 92000.0 to 110000.0
            TimeRange.YEAR_1 -> 40000.0 to 120000.0
        }

        PricePoint(
            timestamp = timestamp,
            price = price.coerceIn(minPrice, maxPrice)
        )
    }
}