package com.calculator.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import com.calculator.ui.components.CavitySelector
import com.calculator.ui.components.ResultCard
import com.calculator.ui.theme.BorderDark
import com.calculator.ui.theme.SurfaceDark
import com.calculator.ui.theme.productionAccent

@Composable
fun ProductionSection(
    production: ProductionState,
    result: ProductionResult?
) {
    val accent = productionAccent(0)
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.10f),
                        SurfaceDark.copy(alpha = 0.98f),
                        SurfaceDark
                    )
                ),
                shape = shape
            )
            .border(1.dp, BorderDark.copy(alpha = 0.95f), shape)
            .padding(horizontal = 16.dp, vertical = 16.dp)
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
