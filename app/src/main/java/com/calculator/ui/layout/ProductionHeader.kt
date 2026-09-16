package com.calculator.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculator.ui.theme.TextPrimary
import com.calculator.ui.theme.TextSecondary
import com.calculator.ui.theme.textColorFor

@Composable
fun ProductionHeader(accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(accent.copy(alpha = 0.75f), accent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                color = textColorFor(accent),
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
        }

        Spacer(Modifier.width(10.dp))

        Column {
            Text(
                text = "Production",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Départ : Maintenant",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
