package com.calculator.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("${hours} h ")
        if (minutes > 0 || hours > 0) append("${minutes} min ")
        append("${seconds} s")
    }
}

fun formatRelativeDateTime(dateTime: LocalDateTime): String {
    val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val today = LocalDate.now()
    val dayDifference = ChronoUnit.DAYS.between(today, dateTime.toLocalDate())

    return when (dayDifference) {
        0L -> "Aujourd'hui à $time"
        1L -> "Demain à $time"
        else -> {
            val dayName = dateTime
                .format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH))
                .replaceFirstChar { it.uppercase(Locale.FRENCH) }
            "$dayName à $time"
        }
    }
}
