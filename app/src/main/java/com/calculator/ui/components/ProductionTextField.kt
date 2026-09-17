package com.calculator.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.calculator.ui.theme.BorderDark
import com.calculator.ui.theme.FieldBackground
import com.calculator.ui.theme.TextPrimary
import com.calculator.ui.theme.TextSecondary

@Composable
fun ProductionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    keyboardType: KeyboardType,
    accent: Color,
    @DrawableRes leadingIcon: Int? = null,
    suffix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(
                text = label,
                maxLines = 1,
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = leadingIcon?.let { iconRes ->
            {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = accent.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        suffix = suffix?.let {
            {
                Text(
                    text = it,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            focusedBorderColor = accent,
            unfocusedBorderColor = BorderDark,
            focusedLabelColor = accent,
            unfocusedLabelColor = TextSecondary,
            cursorColor = accent,
            focusedSuffixColor = accent,
            unfocusedSuffixColor = TextSecondary
        )
    )
}
