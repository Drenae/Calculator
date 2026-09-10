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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.calculator.ui.theme.TextPrimary
import com.calculator.ui.theme.TextSecondary
import com.calculator.util.formatDuration
import com.calculator.util.formatRelativeDateTime

@Composable
fun ResultCard(result: ProductionResult, accent: Color) {
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
                label = "Temps de production",
                value = formatDuration(result.totalSeconds),
                accent = TextPrimary
            )
            ResultDivider()
            ResultColumn(
                modifier = Modifier.weight(1f),
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
    accent: Color
) {
    Column(modifier.padding(horizontal = 12.dp)) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            color = accent,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun ResultDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(52.dp)
            .background(BorderDark.copy(alpha = 0.8f))
    )
}
