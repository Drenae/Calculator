package com.calculator.model

import java.time.LocalDateTime

data class ProductionResult(
    val totalQuantity: Long,
    val totalSeconds: Long,
    val paletteSeconds: Long,
    val unitsPerHour: Double,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
