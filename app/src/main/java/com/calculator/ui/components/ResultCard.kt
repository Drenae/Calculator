package com.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculator.model.ProductionResult
import com.calculator.ui.theme.BorderDark
import com.calculator.ui.theme.SurfaceRaised
import com.calculator.ui.theme.TextPrimary
import com.calculator.ui.theme.TextSecondary
import com.calculator.util.formatDuration
import com.calculator.util.formatRelativeDateTime
import java.util.Locale

@Composable
fun ResultCard(result: ProductionResult, accent: Color) {
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceRaised, shape)
            .border(1.dp, BorderDark, shape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .background(accent, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = "Résultat",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                ResultItem(
                    label = "Temps d'une palette",
                    value = formatDuration(result.paletteSeconds),
                    valueColor = TextPrimary
                )
                Spacer(Modifier.height(16.dp))
                ResultItem(
                    label = "Seaux à l'heure",
                    value = String.format(Locale.getDefault(), "%.0f", result.unitsPerHour),
                    valueColor = TextPrimary
                )
            }

            ResultDivider()

            Column(Modifier.weight(1f)) {
                ResultItem(
                    label = "Temps de production",
                    value = formatDuration(result.totalSeconds),
                    valueColor = TextPrimary
                )
                Spacer(Modifier.height(16.dp))
                ResultItem(
                    label = "Fin estimée",
                    value = formatRelativeDateTime(result.endTime),
                    valueColor = accent
                )
            }
        }
    }
}

@Composable
private fun ResultItem(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(Modifier.padding(horizontal = 10.dp)) {
        Text(
            text = label,
            color = TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            lineHeight = 23.sp
        )
    }
}

@Composable
private fun ResultDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(126.dp)
            .background(BorderDark)
    )
}
