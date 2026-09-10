package com.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
private val GreenAccent = Color(0xFF5FE0A7)
private val PurpleAccent = Color(0xFF8C62FF)
private val OrangeAccent = Color(0xFFFFB23F)
private val TextOnLight = Color(0xFF07111E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme(darkTheme = true, dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = AppBackground) { innerPadding ->
                    ProductionCalculator(Modifier.fillMaxSize().padding(innerPadding))
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
    var alreadyProducedOnCurrentPalette by mutableStateOf("")
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

    productions.forEachIndexed { index, production ->
        val result = calculateProduction(production, nextStart, includeCurrentPalette = index == 0)
        results += result
        if (result != null) nextStart = result.endTime
    }

    LazyColumn(
        modifier = modifier.background(Brush.verticalGradient(listOf(Color(0xFF081522), AppBackground, Color(0xFF050C15)))),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            AppHeader(
                onReset = {
                    productions.clear()
                    productions.add(ProductionState())
                }
            )
        }
        itemsIndexed(productions) { index, production ->
            val result = results[index]
            val chainedStart = if (index == 0) null else results.getOrNull(index - 1)?.endTime
            ProductionSection(index, production, result, chainedStart, index == productions.lastIndex && result != null) {
                productions.add(ProductionState())
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun AppHeader(onReset: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = BorderDark,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height - strokeWidth / 2),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(Color(0xFF46A9FF), Color(0xFF176DE8)))),
            contentAlignment = Alignment.Center
        ) { Text("P", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Calculateur de production", color = TextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Calculez. Planifiez. Produisez.", color = Color(0xFF73A8E8), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.width(6.dp))
        TextButton(
            onClick = onReset,
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(11.dp))
                .background(SurfaceRaised)
                .border(1.dp, BorderDark, RoundedCornerShape(11.dp))
        ) {
            Text(
                text = "↻ Réinit.",
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductionSection(index: Int, production: ProductionState, result: ProductionResult?, chainedStart: LocalDateTime?, canAddNext: Boolean, onAddNext: () -> Unit) {
    val accent = productionAccent(index)
    val textOnAccent = textColorFor(accent)
    Column(Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = accent.copy(alpha = 0.72f),
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - strokeWidth / 2),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height - strokeWidth / 2),
                        strokeWidth = strokeWidth
                    )
                },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                ProductionHeader(index, chainedStart, accent)
                Spacer(Modifier.height(14.dp))
                ProductionInputRow(production, accent, showCurrentPalette = index == 0)

                Spacer(Modifier.height(14.dp))
                Text("Empreintes", color = TextSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(7.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 4, 12).forEach { value ->
                        FilterChip(
                            selected = production.cavityCount == value,
                            onClick = { production.cavityCount = value },
                            label = { Text(value.toString(), Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = if (production.cavityCount == value) FontWeight.Bold else FontWeight.Medium) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(11.dp),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = production.cavityCount == value, borderColor = BorderDark, selectedBorderColor = accent),
                            colors = FilterChipDefaults.filterChipColors(containerColor = SurfaceRaised, labelColor = TextPrimary, selectedContainerColor = accent, selectedLabelColor = textOnAccent)
                        )
                    }
                }
                if (result != null) { Spacer(Modifier.height(14.dp)); ResultCard(result, accent) }
            }
        }
        if (result != null && canAddNext) {
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onAddNext,
                modifier = Modifier.align(Alignment.CenterHorizontally).clip(RoundedCornerShape(14.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF1569D8), Color(0xFF2C91FF)))).padding(horizontal = 12.dp)
            ) { Text("＋  Ajouter une production", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ProductionHeader(index: Int, chainedStart: LocalDateTime?, accent: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(Brush.linearGradient(listOf(accent.copy(alpha = 0.75f), accent))), contentAlignment = Alignment.Center) {
            Text((index + 1).toString(), color = textColorFor(accent), fontWeight = FontWeight.Black, fontSize = 17.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text("Production ${index + 1}", color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                if (index == 0) "Départ : Maintenant" else if (chainedStart != null) "Départ : ${formatRelativeDateTime(chainedStart)}" else "En attente de la production précédente",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProductionInputRow(production: ProductionState, accent: Color, showCurrentPalette: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ProductionTextField(production.paletteCount, { if (it.all(Char::isDigit)) production.paletteCount = it }, "Pal", Modifier.weight(1f), KeyboardType.Number, accent)
        ProductionTextField(production.quantityPerPalette, { if (it.all(Char::isDigit)) production.quantityPerPalette = it }, "Qts/P", Modifier.weight(1.12f), KeyboardType.Number, accent)
        ProductionTextField(
            production.cycleTime,
            { value ->
                val normalized = value.replace(',', '.')
                if (normalized.count { it == '.' } <= 1 && normalized.all { it.isDigit() || it == '.' }) production.cycleTime = value
            },
            "s", Modifier.weight(0.9f), KeyboardType.Decimal, accent
        )
        if (showCurrentPalette) {
            ProductionTextField(
                production.alreadyProducedOnCurrentPalette,
                { if (it.all(Char::isDigit)) production.alreadyProducedOnCurrentPalette = it },
                "En cours", Modifier.weight(1.18f), KeyboardType.Number, accent
            )
        }
    }
}

@Composable
private fun ProductionTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier, keyboardType: KeyboardType, accent: Color, suffix: String? = null) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = modifier,
        label = { Text(label, maxLines = 1) }, suffix = suffix?.let { { Text(it) } }, singleLine = true,
        shape = RoundedCornerShape(13.dp), keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
            focusedContainerColor = FieldBackground, unfocusedContainerColor = FieldBackground,
            focusedBorderColor = accent, unfocusedBorderColor = BorderDark,
            focusedLabelColor = accent, unfocusedLabelColor = TextSecondary, cursorColor = accent,
            focusedSuffixColor = TextSecondary, unfocusedSuffixColor = TextSecondary
        )
    )
}

@Composable
private fun ResultCard(result: ProductionResult, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, accent.copy(alpha = 0.48f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f))
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            ResultColumn(Modifier.weight(1f), "Temps de production", formatDuration(result.totalSeconds), TextPrimary)
            ResultDivider()
            ResultColumn(Modifier.weight(1f), "Fin estimée", formatRelativeDateTime(result.endTime), accent)
        }
    }
}

@Composable
private fun ResultColumn(modifier: Modifier, label: String, value: String, accent: Color) {
    Column(modifier.padding(horizontal = 12.dp)) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(3.dp))
        Text(value, color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, lineHeight = 19.sp)
    }
}

@Composable
private fun ResultDivider() { Box(Modifier.width(1.dp).height(52.dp).background(BorderDark.copy(alpha = 0.8f))) }

private fun textColorFor(background: Color): Color = if (background.luminance() > 0.45f) TextOnLight else Color.White
private fun productionAccent(index: Int): Color = when (index % 3) { 0 -> GreenAccent; 1 -> PurpleAccent; else -> OrangeAccent }

private fun calculateProduction(production: ProductionState, startTime: LocalDateTime, includeCurrentPalette: Boolean = false): ProductionResult? {
    val palettes = production.paletteCount.toLongOrNull()
    val quantity = production.quantityPerPalette.toLongOrNull()
    val cycleSeconds = production.cycleTime.replace(',', '.').toDoubleOrNull()
    val alreadyProduced = production.alreadyProducedOnCurrentPalette.toLongOrNull() ?: 0L

    if (palettes == null || palettes < 0 || quantity == null || quantity <= 0 || cycleSeconds == null || cycleSeconds <= 0) return null
    if (includeCurrentPalette && (alreadyProduced < 0 || alreadyProduced >= quantity)) return null

    val currentPaletteRemaining = if (includeCurrentPalette && alreadyProduced > 0) quantity - alreadyProduced else 0L
    val totalQuantity = (palettes * quantity) + currentPaletteRemaining
    if (totalQuantity <= 0) return null

    val cycleCount = ceil(totalQuantity.toDouble() / production.cavityCount).toLong()
    val totalSeconds = (cycleCount * cycleSeconds).roundToLong()
    return ProductionResult(totalQuantity, totalSeconds, startTime, startTime.plusSeconds(totalSeconds))
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
            val dayName = dateTime.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)).replaceFirstChar { it.uppercase(Locale.FRENCH) }
            "$dayName à $time"
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductionCalculatorPreview() {
    CalculatorTheme(darkTheme = true, dynamicColor = false) { ProductionCalculator(Modifier.fillMaxSize()) }
}
