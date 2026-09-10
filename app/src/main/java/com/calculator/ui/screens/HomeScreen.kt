package com.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calculator.domain.calculateProduction
import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import com.calculator.ui.layout.AppHeader
import com.calculator.ui.layout.ProductionSection
import com.calculator.ui.theme.AppBackground
import com.calculator.ui.theme.CalculatorTheme
import java.time.LocalDateTime

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val productions = remember { mutableStateListOf(ProductionState()) }
    val now = LocalDateTime.now()
    val results = mutableListOf<ProductionResult?>()
    var nextStart = now

    productions.forEachIndexed { index, production ->
        val result = calculateProduction(
            production = production,
            startTime = nextStart,
            includeCurrentPalette = index == 0
        )
        results += result
        if (result != null) nextStart = result.endTime
    }

    LazyColumn(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color(0xFF081522),
                    AppBackground,
                    Color(0xFF050C15)
                )
            )
        )
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
            val chainedStart = if (index == 0) {
                null
            } else {
                results.getOrNull(index - 1)?.endTime
            }

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
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    CalculatorTheme(darkTheme = true, dynamicColor = false) {
        HomeScreen(Modifier.fillMaxSize())
    }
}
