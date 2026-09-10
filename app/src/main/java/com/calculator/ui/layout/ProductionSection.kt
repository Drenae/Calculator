package com.calculator.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calculator.model.ProductionResult
import com.calculator.state.ProductionState
import com.calculator.ui.components.ResultCard
import com.calculator.ui.theme.BorderDark
import com.calculator.ui.theme.SurfaceDark
import com.calculator.ui.theme.SurfaceRaised
import com.calculator.ui.theme.TextPrimary
import com.calculator.ui.theme.TextSecondary
import com.calculator.ui.theme.productionAccent
import com.calculator.ui.theme.textColorFor
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
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
    val textOnAccent = textColorFor(accent)

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
                ProductionHeader(index, chainedStart, accent)

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
                                    fontWeight = if (production.cavityCount == value) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    }
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
                                selectedLabelColor = textOnAccent
                            )
                        )
                    }
                }

                if (result != null) {
                    Spacer(Modifier.height(14.dp))
                    ResultCard(result, accent)
                }
            }
        }

        if (result != null && canAddNext) {
            Spacer(Modifier.height(8.dp))
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
