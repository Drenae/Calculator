package com.calculator.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import com.calculator.ui.components.AddProductionButton
import com.calculator.ui.components.CavitySelector
import com.calculator.ui.components.ResultCard
import com.calculator.ui.theme.SurfaceDark
import com.calculator.ui.theme.TextSecondary
import com.calculator.ui.theme.productionAccent
import java.time.LocalDateTime

@Composable
fun ProductionSection(
    index: Int,
    production: ProductionState,
    result: ProductionResult?,
    chainedStart: LocalDateTime?,
    canAddNext: Boolean,
    onAddNext: () -> Unit
) {
    val accent = productionAccent(index)

    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark.copy(alpha = 0.96f))
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = accent.copy(alpha = 0.72f),
                        start = Offset(0f, size.height - strokeWidth / 2),
                        end = Offset(size.width, size.height - strokeWidth / 2),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            Column(Modifier.padding(10.dp)) {
                ProductionHeader(
                    index = index,
                    chainedStart = chainedStart,
                    accent = accent
                )

                Spacer(Modifier.height(14.dp))

                ProductionInputRow(
                    production = production,
                    accent = accent,
                    showCurrentPalette = index == 0
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Empreintes",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(7.dp))

                CavitySelector(
                    selectedCavityCount = production.cavityCount,
                    accent = accent,
                    onCavitySelected = { production.cavityCount = it }
                )

                if (result != null) {
                    Spacer(Modifier.height(14.dp))
                    ResultCard(result = result, accent = accent)
                }
            }
        }

        if (result != null && canAddNext) {
            Spacer(Modifier.height(8.dp))
            AddProductionButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onAddNext
            )
        }
    }
}
