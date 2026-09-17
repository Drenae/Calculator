package com.calculator.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun AppHeader(onReset: () -> Unit) {
    val accent = Color(0xFF19D3AE)
    val headerBackground = Color(0xFF101719)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerBackground)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF27E1BB),
                            Color(0xFF0BAF91)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                color = Color(0xFF07110F),
                fontWeight = FontWeight.Black,
                fontSize = 21.sp
            )
        }

        Spacer(Modifier.width(13.dp))

        Text(
            text = "Production",
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        TextButton(
            onClick = onReset,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
            modifier = Modifier
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = accent.copy(alpha = 0.28f),
                    shape = CircleShape
                )
        ) {
            Text(
                text = "↻  Réinit.",
                color = accent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}
