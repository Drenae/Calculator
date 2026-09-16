package com.calculator.ui.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import com.calculator.ui.components.CavitySelector
import com.calculator.ui.components.ResultCard
import com.calculator.ui.theme.productionAccent

@Composable
fun ProductionSection(
    production: ProductionState,
    result: ProductionResult?
) {
    val accent = productionAccent(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ProductionHeader(accent = accent)

        Spacer(Modifier.height(18.dp))

        ProductionInputRow(
            production = production,
            accent = accent
        )

        Spacer(Modifier.height(20.dp))

        CavitySelector(
            selectedCavityCount = production.cavityCount,
            accent = accent,
            onCavitySelected = { production.cavityCount = it }
        )

        if (result != null) {
            Spacer(Modifier.height(20.dp))
            ResultCard(result = result, accent = accent)
        }
    }
}
