package com.calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calculator.ui.theme.BorderDark
import com.calculator.ui.theme.SurfaceRaised
import com.calculator.ui.theme.TextPrimary
import com.calculator.ui.theme.textColorFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CavitySelector(
    selectedCavityCount: Int,
    accent: Color,
    onCavitySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(1, 2, 4, 12).forEach { value ->
            val selected = selectedCavityCount == value

            FilterChip(
                selected = selected,
                onClick = { onCavitySelected(value) },
                label = {
                    Text(
                        text = value.toString(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(11.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = BorderDark,
                    selectedBorderColor = accent
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = SurfaceRaised,
                    labelColor = TextPrimary,
                    selectedContainerColor = accent,
                    selectedLabelColor = textColorFor(accent)
                )
            )
        }
    }
}
