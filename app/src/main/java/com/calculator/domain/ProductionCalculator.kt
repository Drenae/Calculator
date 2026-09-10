package com.calculator.domain

import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import java.time.LocalDateTime
import kotlin.math.ceil
import kotlin.math.roundToLong

fun calculateProduction(
    production: ProductionState,
    startTime: LocalDateTime,
    includeCurrentPalette: Boolean = false
): ProductionResult? {
    val palettes = production.paletteCount.toLongOrNull()
    val quantity = production.quantityPerPalette.toLongOrNull()
    val cycleSeconds = production.cycleTime.replace(',', '.').toDoubleOrNull()
    val alreadyProduced = production.alreadyProducedOnCurrentPalette.toLongOrNull() ?: 0L

    if (
        palettes == null || palettes < 0 ||
        quantity == null || quantity <= 0 ||
        cycleSeconds == null || cycleSeconds <= 0
    ) return null

    if (includeCurrentPalette && (alreadyProduced < 0 || alreadyProduced >= quantity)) return null

    val currentPaletteRemaining =
        if (includeCurrentPalette && alreadyProduced > 0) quantity - alreadyProduced else 0L

    val totalQuantity = (palettes * quantity) + currentPaletteRemaining
    if (totalQuantity <= 0) return null

    val cycleCount = ceil(totalQuantity.toDouble() / production.cavityCount).toLong()
    val totalSeconds = (cycleCount * cycleSeconds).roundToLong()

    return ProductionResult(
        totalQuantity = totalQuantity,
        totalSeconds = totalSeconds,
        startTime = startTime,
        endTime = startTime.plusSeconds(totalSeconds)
    )
}
