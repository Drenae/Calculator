package com.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calculator.domain.calculateProduction
import com.calculator.state.ProductionState
import com.calculator.ui.layout.AppHeader
import com.calculator.ui.layout.ProductionSection
import com.calculator.ui.theme.AppBackground
import com.calculator.ui.theme.CalculatorTheme
import java.time.LocalDateTime

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val production = remember { ProductionState() }
    val result = calculateProduction(
        production = production,
        startTime = LocalDateTime.now()
    )

    LazyColumn(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color(0xFF17191C),
                    AppBackground,
                    Color(0xFF0D0E10)
                )
            )
        )
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            AppHeader(
                onReset = {
                    production.paletteCount = ""
                    production.quantityPerPalette = ""
                    production.cycleTime = ""
                    production.cavityCount = 2
                }
            )
        }

        item {
            ProductionSection(
                production = production,
                result = result
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
