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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calculator.ui.theme.CalculatorTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

private class ProductionState {
    var paletteCount by mutableStateOf("")
    var quantityPerPalette by mutableStateOf("")
    var cycleTime by mutableStateOf("")
    var cavityCount by mutableIntStateOf(1)
}

private data class ProductionResult(
    val totalQuantity: Long,
    val totalSeconds: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

@Composable
fun ProductionCalculator(modifier: Modifier = Modifier) {
    val productions = remember { mutableStateListOf(ProductionState()) }
    val now = LocalDateTime.now()

    val results = mutableListOf<ProductionResult?>()
    var nextStart = now

    productions.forEach { production ->
        val result = calculateProduction(production, nextStart)
        results += result
        if (result != null) {
            nextStart = result.endTime
        }
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Calculateur de production",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        itemsIndexed(productions) { index, production ->
            val result = results[index]
            val chainedStart = if (index == 0) null else results.getOrNull(index - 1)?.endTime

            ProductionSection(
                index = index,
                production = production,
                result = result,
                chainedStart = chainedStart,
                canAddNext = index == productions.lastIndex && result != null,
                onAddNext = { productions.add(ProductionState()) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductionSection(
    index: Int,
    production: ProductionState,
    result: ProductionResult?,
    chainedStart: LocalDateTime?,
    canAddNext: Boolean,
    onAddNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (index > 0) {
            Text(
                text = "Production ${index + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (chainedStart != null) {
                Text(
                    text = "Départ : ${formatRelativeDateTime(chainedStart)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = production.paletteCount,
                onValueChange = { value ->
                    if (value.all(Char::isDigit)) production.paletteCount = value
                },
                modifier = Modifier.weight(1f),
                label = { Text("Palettes") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = production.quantityPerPalette,
                onValueChange = { value ->
                    if (value.all(Char::isDigit)) production.quantityPerPalette = value
                },
                modifier = Modifier.weight(1.15f),
                label = { Text("Quantité / P") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = production.cycleTime,
                onValueChange = { value ->
                    val normalized = value.replace(',', '.')
                    if (normalized.count { it == '.' } <= 1 &&
                        normalized.all { it.isDigit() || it == '.' }
                    ) {
                        production.cycleTime = value
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Cycle") },
                suffix = { Text("s") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Empreintes",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1, 2, 4, 12).forEach { value ->
                FilterChip(
                    selected = production.cavityCount == value,
                    onClick = { production.cavityCount = value },
                    label = { Text(value.toString()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (result != null) {
            Spacer(modifier = Modifier.height(14.dp))
            ResultCard(result = result)

            if (canAddNext) {
                TextButton(
                    onClick = onAddNext,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("+ Ajouter une production")
                }
            }
        }
    }
}

@Composable
private fun ResultCard(result: ProductionResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${formatNumber(result.totalQuantity)} pièces",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Temps de production : ${formatDuration(result.totalSeconds)}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Fin estimée : ${formatRelativeDateTime(result.endTime)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun calculateProduction(
    production: ProductionState,
    startTime: LocalDateTime
): ProductionResult? {
    val palettes = production.paletteCount.toLongOrNull()
    val quantity = production.quantityPerPalette.toLongOrNull()
    val cycleSeconds = production.cycleTime.replace(',', '.').toDoubleOrNull()

    if (palettes == null || palettes <= 0 ||
        quantity == null || quantity <= 0 ||
        cycleSeconds == null || cycleSeconds <= 0
    ) {
        return null
    }

    val totalQuantity = palettes * quantity
    val cycleCount = ceil(totalQuantity.toDouble() / production.cavityCount).toLong()
    val totalSeconds = (cycleCount * cycleSeconds).roundToLong()

    return ProductionResult(
        totalQuantity = totalQuantity,
        totalSeconds = totalSeconds,
        startTime = startTime,
        endTime = startTime.plusSeconds(totalSeconds)
    )
}

private fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("${hours} h ")
        if (minutes > 0 || hours > 0) append("${minutes} min ")
        append("${seconds} s")
    }
}

private fun formatRelativeDateTime(dateTime: LocalDateTime): String {
    val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val today = LocalDate.now()
    val dayDifference = ChronoUnit.DAYS.between(today, dateTime.toLocalDate())

    return when (dayDifference) {
        0L -> "Aujourd'hui à $time"
        1L -> "Demain à $time"
        else -> {
            val dayName = dateTime.format(
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
