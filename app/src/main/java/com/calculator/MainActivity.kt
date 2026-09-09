package com.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculator.ui.theme.CalculatorTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToLong

private val AppBackground = Color(0xFF07111E)
private val SurfaceDark = Color(0xFF0E1A2A)
private val SurfaceRaised = Color(0xFF132236)
private val FieldBackground = Color(0xFF101D2C)
private val BorderDark = Color(0xFF2C4058)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextSecondary = Color(0xFF9DB1CC)
private val BlueAccent = Color(0xFF3295FF)
private val GreenAccent = Color(0xFF5FE0A7)
private val PurpleAccent = Color(0xFF8C62FF)
private val OrangeAccent = Color(0xFFFFB23F)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme(darkTheme = true, dynamicColor = false) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = AppBackground
                ) { innerPadding ->
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
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF081522), AppBackground, Color(0xFF050C15))
                )
            )
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            AppHeader()
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
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF46A9FF), Color(0xFF176DE8))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Calculateur de production",
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Calculez. Planifiez. Produisez.",
                color = Color(0xFF73A8E8),
                style = MaterialTheme.typography.bodyMedium
            )
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
    val accent = productionAccent(index)

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, accent.copy(alpha = 0.72f), RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProductionHeader(
                    index = index,
                    chainedStart = chainedStart,
                    accent = accent
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProductionInputRow(production = production, accent = accent)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Empreintes",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 4, 12).forEach { value ->
                        FilterChip(
                            selected = production.cavityCount == value,
                            onClick = { production.cavityCount = value },
                            label = {
                                Text(
                                    text = value.toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (production.cavityCount == value) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(11.dp),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = production.cavityCount == value,
                                borderColor = BorderDark,
                                selectedBorderColor = accent
                            ),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceRaised,
                                labelColor = TextPrimary,
                                selectedContainerColor = accent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (result != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ResultCard(result = result, accent = accent)
                }
            }
        }

        if (result != null && canAddNext) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onAddNext,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1569D8), Color(0xFF2C91FF))
                        )
                    )
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "＋  Ajouter une production",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProductionHeader(
    index: Int,
    chainedStart: LocalDateTime?,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(accent.copy(alpha = 0.75f), accent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (index + 1).toString(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = "Production ${index + 1}",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (index == 0) {
                    "Départ : Maintenant"
                } else if (chainedStart != null) {
                    "Départ : ${formatRelativeDateTime(chainedStart)}"
                } else {
                    "En attente de la production précédente"
                },
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductionInputRow(
    production: ProductionState,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductionTextField(
            value = production.paletteCount,
            onValueChange = { value ->
                if (value.all(Char::isDigit)) production.paletteCount = value
            },
            label = "Palettes",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            accent = accent
        )

        ProductionTextField(
            value = production.quantityPerPalette,
            onValueChange = { value ->
                if (value.all(Char::isDigit)) production.quantityPerPalette = value
            },
            label = "Quantité / P",
            modifier = Modifier.weight(1.18f),
            keyboardType = KeyboardType.Number,
            accent = accent
        )

        ProductionTextField(
            value = production.cycleTime,
            onValueChange = { value ->
                val normalized = value.replace(',', '.')
                if (normalized.count { it == '.' } <= 1 &&
                    normalized.all { it.isDigit() || it == '.' }
                ) {
                    production.cycleTime = value
                }
            },
            label = "Cycle",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Decimal,
            accent = accent,
            suffix = "s"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    keyboardType: KeyboardType,
    accent: Color,
    suffix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label, maxLines = 1) },
        suffix = suffix?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(13.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            focusedBorderColor = accent,
            unfocusedBorderColor = BorderDark,
            focusedLabelColor = accent,
            unfocusedLabelColor = TextSecondary,
            cursorColor = accent,
            focusedSuffixColor = TextSecondary,
            unfocusedSuffixColor = TextSecondary
        )
    )
}

@Composable
private fun ResultCard(
    result: ProductionResult,
    accent: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = 0.48f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResultColumn(
                modifier = Modifier.weight(1f),
                label = "Total à produire",
                value = formatNumber(result.totalQuantity),
                subValue = "pièces",
                accent = accent
            )

            ResultDivider()

            ResultColumn(
                modifier = Modifier.weight(1.12f),
                label = "Temps de production",
                value = formatDuration(result.totalSeconds),
                accent = TextPrimary
            )

            ResultDivider()

            ResultColumn(
                modifier = Modifier.weight(1.15f),
                label = "Fin estimée",
                value = formatRelativeDateTime(result.endTime),
                accent = accent
            )
        }
    }
}

@Composable
private fun ResultColumn(
    modifier: Modifier,
    label: String,
    value: String,
    accent: Color,
    subValue: String? = null
) {
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            text = label,
            color = TextSecondary,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = accent,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            lineHeight = 19.sp
        )
        if (subValue != null) {
            Text(
                text = subValue,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ResultDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(52.dp)
            .background(BorderDark.copy(alpha = 0.8f))
    )
}

private fun productionAccent(index: Int): Color = when (index % 3) {
    0 -> GreenAccent
    1 -> PurpleAccent
    else -> OrangeAccent
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
    CalculatorTheme(darkTheme = true, dynamicColor = false) {
        ProductionCalculator(modifier = Modifier.fillMaxSize())
    }
}
