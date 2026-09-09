package com.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calculator.ui.theme.CalculatorTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToLong

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductionCalculator(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionCalculator(modifier: Modifier = Modifier) {
    var paletteCount by remember { mutableStateOf("") }
    var quantityPerPalette by remember { mutableStateOf("") }
    var cycleTime by remember { mutableStateOf("") }
    var cavityCount by remember { mutableIntStateOf(1) }

    val palettes = paletteCount.toLongOrNull()
    val quantity = quantityPerPalette.toLongOrNull()
    val cycleSeconds = cycleTime.replace(',', '.').toDoubleOrNull()

    val isValid = palettes != null && palettes > 0 &&
        quantity != null && quantity > 0 &&
        cycleSeconds != null && cycleSeconds > 0

    val totalQuantity = if (isValid) palettes!! * quantity!! else null
    val cycleCount = totalQuantity?.let { ceil(it.toDouble() / cavityCount).toLong() }
    val totalSeconds = if (cycleCount != null && cycleSeconds != null) {
        (cycleCount * cycleSeconds).roundToLong()
    } else {
        null
    }

    val startTime = if (totalSeconds != null) LocalDateTime.now() else null
    val endTime = if (startTime != null && totalSeconds != null) {
        startTime.plusSeconds(totalSeconds)
    } else {
        null
    }

    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Calculateur de production",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = paletteCount,
            onValueChange = { value ->
                if (value.all(Char::isDigit)) paletteCount = value
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre de palettes") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = quantityPerPalette,
            onValueChange = { value ->
                if (value.all(Char::isDigit)) quantityPerPalette = value
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantité par palette") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = cycleTime,
            onValueChange = { value ->
                val normalized = value.replace(',', '.')
                if (normalized.count { it == '.' } <= 1 &&
                    normalized.all { it.isDigit() || it == '.' }
                ) {
                    cycleTime = value
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Temps de cycle") },
            suffix = { Text("sec") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Nombre d'empreintes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(1, 2, 4, 12).forEach { value ->
                FilterChip(
                    selected = cavityCount == value,
                    onClick = { cavityCount = value },
                    label = { Text(value.toString()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (totalQuantity != null && totalSeconds != null && startTime != null && endTime != null) {
            ResultCard(
                totalQuantity = totalQuantity,
                totalSeconds = totalSeconds,
                startTime = startTime,
                endTime = endTime
            )
        }
    }
}

@Composable
private fun ResultCard(
    totalQuantity: Long,
    totalSeconds: Long,
    startTime: LocalDateTime,
    endTime: LocalDateTime
) {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val durationText = buildString {
        if (hours > 0) append("${hours} h ")
        if (minutes > 0 || hours > 0) append("${minutes} min ")
        append("${seconds} s")
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Quantité totale",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "${formatNumber(totalQuantity)} pièces",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Temps de production",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = durationText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Fin estimée",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = formatEstimatedEnd(startTime, endTime),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatEstimatedEnd(startTime: LocalDateTime, endTime: LocalDateTime): String {
    val time = endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val dayDifference = java.time.temporal.ChronoUnit.DAYS.between(
        startTime.toLocalDate(),
        endTime.toLocalDate()
    )

    return when (dayDifference) {
        0L -> "Aujourd'hui à $time"
        1L -> "Demain à $time"
        else -> {
            val dayName = endTime.format(
                DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)
            ).replaceFirstChar { it.uppercase(Locale.FRENCH) }
            "$dayName à $time"
        }
    }
}

private fun formatNumber(value: Long): String =
    String.format("%,d", value).replace(',', ' ')

@Preview(showBackground = true)
@Composable
fun ProductionCalculatorPreview() {
    CalculatorTheme {
        ProductionCalculator(modifier = Modifier.fillMaxSize())
    }
}
