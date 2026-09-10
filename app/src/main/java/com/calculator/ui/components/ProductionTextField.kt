package com.calculator.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    leadingIcon: ImageVector? = null,
    suffix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label, maxLines = 1) },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        suffix = suffix?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(13.dp),
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
            focusedLeadingIconColor = accent,
            unfocusedLeadingIconColor = TextSecondary,
            focusedSuffixColor = TextSecondary,
            unfocusedSuffixColor = TextSecondary
        )
    )
}
