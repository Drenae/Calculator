package com.calculator.domain

import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import java.time.LocalDateTime
import kotlin.math.ceil
import kotlin.math.roundToLong

fun calculateProduction(
    production: ProductionState,
    startTime: LocalDateTime
): ProductionResult? {
    val palettes = production.paletteCount.toLongOrNull()
    val quantity = production.quantityPerPalette.toLongOrNull()
    val cycleSeconds = production.cycleTime.replace(',', '.').toDoubleOrNull()

    if (
        palettes == null || palettes <= 0 ||
        quantity == null || quantity <= 0 ||
        cycleSeconds == null || cycleSeconds <= 0
    ) return null

    val totalQuantity = palettes * quantity
    val cycleCount = ceil(totalQuantity.toDouble() / production.cavityCount).toLong()
    val totalSeconds = (cycleCount * cycleSeconds).roundToLong()
    val paletteCycleCount = ceil(quantity.toDouble() / production.cavityCount).toLong()
    val paletteSeconds = (paletteCycleCount * cycleSeconds).roundToLong()
    val unitsPerHour = production.cavityCount * 3600.0 / cycleSeconds

    return ProductionResult(
        totalQuantity = totalQuantity,
        totalSeconds = totalSeconds,
        paletteSeconds = paletteSeconds,
        unitsPerHour = unitsPerHour,
        startTime = startTime,
        endTime = startTime.plusSeconds(totalSeconds)
    )
}
