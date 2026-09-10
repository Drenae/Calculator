package com.calculator.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.calculator.R
import com.calculator.state.ProductionState
import com.calculator.ui.components.ProductionTextField

@Composable
fun ProductionInputRow(
    production: ProductionState,
    accent: Color,
    showCurrentPalette: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ProductionTextField(
            value = production.paletteCount,
            onValueChange = {
                if (it.all(Char::isDigit)) production.paletteCount = it
            },
            label = "Pal",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number,
            accent = accent,
            leadingIcon = R.drawable.ic_palette
        )

        ProductionTextField(
            value = production.quantityPerPalette,
            onValueChange = {
                if (it.all(Char::isDigit)) production.quantityPerPalette = it
            },
            label = "Qts/P",
            modifier = Modifier.weight(1.12f),
            keyboardType = KeyboardType.Number,
            accent = accent,
            leadingIcon = R.drawable.ic_quantity_box
        )

        ProductionTextField(
            value = production.cycleTime,
            onValueChange = { value ->
                val normalized = value.replace(',', '.')
                if (
                    normalized.count { it == '.' } <= 1 &&
                    normalized.all { it.isDigit() || it == '.' }
                ) {
                    production.cycleTime = value
                }
            },
            label = "s",
            modifier = Modifier.weight(0.9f),
            keyboardType = KeyboardType.Decimal,
            accent = accent,
            leadingIcon = R.drawable.ic_cycle_time
        )

        if (showCurrentPalette) {
            ProductionTextField(
                value = production.alreadyProducedOnCurrentPalette,
                onValueChange = {
                    if (it.all(Char::isDigit)) production.alreadyProducedOnCurrentPalette = it
                },
                label = "En cours",
                modifier = Modifier.weight(1.18f),
                keyboardType = KeyboardType.Number,
                accent = accent,
                leadingIcon = R.drawable.ic_current_palette
            )
        }
    }
}
